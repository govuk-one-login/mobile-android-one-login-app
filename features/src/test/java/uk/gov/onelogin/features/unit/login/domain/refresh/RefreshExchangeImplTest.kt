package uk.gov.onelogin.features.unit.login.domain.refresh

import android.content.Context
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Named.named
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.anyVararg
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.authentication.login.refresh.DemonstratingProofOfPossessionManager
import uk.gov.android.authentication.login.refresh.SignedDPoP
import uk.gov.android.network.client.v2.GenericHttpResponse
import uk.gov.android.network.client.v2.GenericResponseException
import uk.gov.android.network.client.v2.StubHttpClient
import uk.gov.android.network.client.v2.TestHttpResponse
import uk.gov.android.network.service.ClientAttestationException
import uk.gov.android.network.service.ServiceException
import uk.gov.android.network.service.v2.DefaultNetworkService
import uk.gov.android.network.service.v2.NetworkService
import uk.gov.logging.api.v3.LogLevel
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasException
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasTag
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.isLogLevel
import uk.gov.logging.api.v3.matchers.MemorisedLoggerMatchers.hasSize
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.tokendata.LoginTokens
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.tokens.domain.retrieve.GetFromEncryptedSecureStore
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.core.utils.SystemTimeProvider
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityException
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationResult
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchange
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchangeImpl
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchangeResult
import uk.gov.onelogin.features.login.domain.validateWalletStoreId.ValidateWalletStoreId
import uk.gov.onelogin.features.network.provider.ClientAttestationProviderImpl
import uk.gov.onelogin.features.network.provider.DPoPProviderImpl
import java.util.stream.Stream
import kotlin.test.assertEquals

@Suppress("LargeClass")
class RefreshExchangeImplTest {
    private val fragmentContext: FragmentActivity = mock()
    private val context: Context = mock()
    private val getPersistentId: GetPersistentId = mock()
    private val isRefreshTokenExpired: IsTokenExpired = mock()
    private val appIntegrity: AppIntegrity = mock()
    private val dPoPManager: DemonstratingProofOfPossessionManager = mock()
    private val httpClient: StubHttpClient = StubHttpClient()
    private val networkService: NetworkService = DefaultNetworkService(httpClient).apply {
        setClientAttestationProvider(ClientAttestationProviderImpl(appIntegrity))
        setDPoPProvider(DPoPProviderImpl(context, dPoPManager))
    }
    private val getFromEncryptedSecureStore: GetFromEncryptedSecureStore = mock()
    private val saveTokenExpiry: SaveTokenExpiry = mock()
    private val tokenRepository: TokenRepository = mock()
    private val saveTokens: SaveTokens = mock()
    private val logger: MemorisedLogger = MemorisedLogger()
    private val timeProvider: SystemTimeProvider = mock()
    private val validateWalletStoreId: ValidateWalletStoreId = mock()
    private val refreshExchange: RefreshExchange = RefreshExchangeImpl(
        context = context,
        getPersistentId = getPersistentId,
        isRefreshTokenExpired = isRefreshTokenExpired,
        networkService = networkService,
        getFromEncryptedSecureStore = getFromEncryptedSecureStore,
        saveTokenExpiry = saveTokenExpiry,
        tokenRepository = tokenRepository,
        saveTokens = saveTokens,
        logger = logger,
        timeProvider = timeProvider,
        validateWalletStoreId = validateWalletStoreId
    )

    @BeforeEach
    fun setup() = runTest {
        whenever(context.getString(any(), anyVararg()))
            .thenReturn("https://test/test")
        whenever(context.getString(any()))
            .thenReturn("test")

        // Configure defaults for happy path
        whenever(getPersistentId()).thenReturn("testId")
        whenever(isRefreshTokenExpired()).thenReturn(false)
        whenever(appIntegrity.getClientAttestation())
            .thenReturn(AttestationResult.Success("clientAttestation"))
        whenever(timeProvider.calculateExpiryTime(any())).thenReturn(100)
        givenLocalAuthStatus(
            LocalAuthStatus.Success(
                mapOf(
                    AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                    AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                )
            )
        )
        whenever(dPoPManager.generateDPoP(any()))
            .thenReturn(SignedDPoP.Success("signedDPoP"))
        whenever(appIntegrity.getProofOfPossession())
            .thenReturn(SignedPoP.Success("signedPoP"))
        whenever(validateWalletStoreId.invoke())
            .thenReturn(true)
        httpClient.response = refreshApiSuccessResponse
    }

    @Test
    fun `successful refresh exchange`() =
        runTest {
            val result = getTokens()

            assertThat(logger, hasSize(0))

            assertLoginTokensSaved()
            assertEquals(RefreshExchangeResult.Success, result)
        }

    @Test
    fun `given persistent session ID is null, then result is first time user`() =
        runTest {
            whenever(getPersistentId()).thenReturn(null)

            val result = getTokens()

            assertNothingSaved()
            assertEquals(RefreshExchangeResult.FirstTimeUser, result)
        }

    @Test
    fun `given persistent session ID is empty, then result is first time user`() =
        runTest {
            whenever(getPersistentId()).thenReturn("")

            val result = getTokens()

            assertNothingSaved()
            assertNothingLogged()
            assertEquals(RefreshExchangeResult.FirstTimeUser, result)
        }

    @Test
    fun `given refresh token is expired, then result is re-auth required`() =
        runTest {
            whenever(isRefreshTokenExpired()).thenReturn(true)

            val result = getTokens()

            assertNothingSaved()
            assertNothingLogged()
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `given wallet store id validation fails, then result is re-auth required`() =
        runTest {
            whenever(validateWalletStoreId.invoke()).thenReturn(false)

            val result = getTokens()

            assertNothingSaved()
            assertNothingLogged()
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `given saved client attestation is null but not required anyway, then result is still success`() =
        runTest {
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.NotRequired(null))

            val result = getTokens()

            assertLoginTokensSaved()
            assertEquals(RefreshExchangeResult.Success, result)
        }

    @Test
    fun `given client attestation is expired, then result is client attestation failure`() =
        runTest {
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(
                    AttestationResult.Failure(
                        type = AppIntegrityException.AppIntegrityErrorType.GENERIC,
                        error = Exception("Client Attestation failure!")
                    )
                )

            val result = getTokens()

            assertNothingSaved()
            assertEquals(RefreshExchangeResult.ClientAttestationFailure, result)
        }

    @Test
    fun `given client attestation is saved but not required, then result is success`() =
        runTest {
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.NotRequired("savedAttestation"))

            val result = getTokens()

            assertLoginTokensSaved()
            assertEquals(RefreshExchangeResult.Success, result)
        }

    @Test
    fun `given failure generating Demonstrating PoP, then result is re-auth required`() =
        runTest {
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Failure("Failure"))

            val result = getTokens()

            assertErrorLogged("DPoP provider failed to fetch refresh DPoP proof", ServiceException::class.java)
            assertNothingSaved()
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `given failure generating Demonstrating PoP with exception, then result is re-auth required`() =
        runTest {
            val exp = RefreshExchangeImpl.Companion.RefreshExchangeException("error")
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Failure("Failure", exp))

            val result = getTokens()

            assertErrorLogged("DPoP provider failed to fetch refresh DPoP proof")
            assertNothingSaved()
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `given failure with generating app integrity PoP, then result is client attestation failure`() =
        runTest {
            whenever(appIntegrity.getProofOfPossession())
                .thenReturn(SignedPoP.Failure("Failure"))

            val result = getTokens()

            assertErrorLogged(
                "Attestation provider failed to fetch client attestation",
                ClientAttestationException::class.java
            )
            assertNothingSaved()
            assertEquals(RefreshExchangeResult.ClientAttestationFailure, result)
        }

    @Test
    fun `given network api response failure, then result is re-auth required`() =
        runTest {
            httpClient.exception =
                GenericResponseException(
                    TestHttpResponse.internalServerError,
                    IllegalStateException()
                )

            val result = getTokens()

            assertErrorLogged("API responded with 500")
            assertNothingSaved()
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `given network error, result is re-auth required`() =
        runTest {
            httpClient.exception = IOException()

            val result = getTokens()

            assertErrorLogged("Network transport error")
            assertNothingSaved()
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `given api responds with null refresh token, result is success`() =
        runTest {
            httpClient.response = refreshApiNoRefreshTokenResponse

            val result = getTokens()

            assertLoginTokensSaved(expiriesSaved = 1)
            assertEquals(RefreshExchangeResult.Success, result)
        }

    @ParameterizedTest
    @MethodSource("getFromEncryptedSecureStoreErrors")
    fun `given error in local auth status`(
        returnedLocalAuthStatus: LocalAuthStatus,
        expected: RefreshExchangeResult
    ) = runTest {
        givenLocalAuthStatus(returnedLocalAuthStatus)

        val result = getTokens()

        assertNothingSaved()
        assertEquals(expected, result)
    }

    private suspend fun getTokens(): RefreshExchangeResult {
        lateinit var result: RefreshExchangeResult
        refreshExchange.getTokens(
            fragmentContext,
            handleResult = {
                result = it
            }
        )
        return result
    }

    private suspend fun givenLocalAuthStatus(localAuthStatus: LocalAuthStatus) {
        whenever(
            getFromEncryptedSecureStore(
                any(),
                anyVararg(),
                callback = any(),
            )
        ).thenAnswer {
            it.getArgument<(LocalAuthStatus) -> Unit>(2).invoke(localAuthStatus)
        }
    }

    private suspend fun assertLoginTokensSaved(
        loginTokens: LoginTokens = expectedLoginTokens,
        expiriesSaved: Int = 2,
    ) {
        verify(tokenRepository).setTokenResponse(loginTokens)
        // Check that both refresh token and access token expiries are saved
        verify(saveTokenExpiry, times(expiriesSaved)).saveExp(anyVararg())
    }

    private fun assertNothingSaved() {
        verifyNoInteractions(saveTokenExpiry)
        verify(tokenRepository, never()).setTokenResponse(any())
        verify(tokenRepository, never()).clearTokenResponse()
        verifyNoInteractions(saveTokens)
    }

    private fun assertNothingLogged() {
        assertThat(logger, hasSize(0))
    }

    private fun assertErrorLogged(
        message: String,
        exceptionClass: Class<*>? = null,
    ) {
        val exceptionMatcher = if (exceptionClass != null) {
            hasException(instanceOf(exceptionClass))
        } else {
            anything()
        }

        assertThat(
            logger,
            hasItem(
                allOf(
                    isLogLevel(LogLevel.Error),
                    hasTag(RefreshExchangeImpl.REFRESH_ERROR_TAG),
                    hasMessage(message),
                    exceptionMatcher,
                )
            )
        )
    }

    companion object {
        private val expectedLoginTokens = LoginTokens(
            tokenType = "Bearer",
            accessToken = "accessToken",
            accessTokenExpirationTime = 100,
            idToken = "testIdToken",
        )

        private val refreshApiSuccessResponse = GenericHttpResponse(
            200,
            "{\n" +
                    "    \"access_token\": \"accessToken\",\n" +
                    "    \"refresh_token\": \"refreshToken\",\n" +
                    "    \"token_type\": \"Bearer\",\n" +
                    "    \"expires_in\": 1\n" +
                    "}"
        )
        private val refreshApiNoRefreshTokenResponse = GenericHttpResponse(
            200,
            "{\n" +
                    "    \"access_token\": \"accessToken\",\n" +
                    "    \"token_type\": \"Bearer\",\n" +
                    "    \"expires_in\": 1\n" +
                    "}"
        )

        @JvmStatic
        @Suppress("LongMethod")
        fun getFromEncryptedSecureStoreErrors(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    LocalAuthStatus.FirstTimeUser,
                    RefreshExchangeResult.FirstTimeUser
                ),
                Arguments.of(
                    LocalAuthStatus.UnrecoverableError,
                    RefreshExchangeResult.UnrecoverableError
                ),
                Arguments.of(
                    LocalAuthStatus.UserCancelledBioPrompt,
                    RefreshExchangeResult.UserCancelledBioPrompt
                ),
                Arguments.of(
                    LocalAuthStatus.ReauthRequired,
                    RefreshExchangeResult.ReauthRequired
                ),
                Arguments.of(
                    LocalAuthStatus.Success(null),
                    RefreshExchangeResult.ReauthRequired
                ),
                Arguments.of(
                    named(
                        "Empty tokens",
                        LocalAuthStatus.Success(
                            mapOf(
                                AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "",
                                AuthTokenStoreKeys.ID_TOKEN_KEY to ""
                            )
                        )
                    ),
                    RefreshExchangeResult.ReauthRequired
                ),
                Arguments.of(
                    named(
                        "Empty refresh token",
                        LocalAuthStatus.Success(
                            mapOf(
                                AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "",
                                AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                            )
                        )
                    ),
                    RefreshExchangeResult.ReauthRequired
                ),
                Arguments.of(
                    named(
                        "Empty id token",
                        LocalAuthStatus.Success(
                            mapOf(
                                AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                                AuthTokenStoreKeys.ID_TOKEN_KEY to ""
                            )
                        )
                    ),
                    RefreshExchangeResult.ReauthRequired
                ),
                Arguments.of(
                    named(
                        "Missing tokens",
                        LocalAuthStatus.Success(emptyMap())
                    ),
                    RefreshExchangeResult.ReauthRequired
                ),
                Arguments.of(
                    named(
                        "Missing refresh token",
                        LocalAuthStatus.Success(
                            mapOf(
                                AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                            )
                        )
                    ),
                    RefreshExchangeResult.ReauthRequired
                ),
                Arguments.of(
                    named(
                        "Missing ID token",
                        LocalAuthStatus.Success(
                            mapOf(
                                AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken"
                            )
                        )
                    ),
                    RefreshExchangeResult.ReauthRequired
                ),
            )
    }
}
