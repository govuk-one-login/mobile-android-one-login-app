package uk.gov.onelogin.features.unit.login.domain.refresh

import android.content.Context
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.anyVararg
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.authentication.login.refresh.DemonstratingProofOfPossessionManager
import uk.gov.android.authentication.login.refresh.SignedDPoP
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
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
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationResult
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchange
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchangeImpl
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchangeImpl.Companion.ATTESTATION_POP_GENERATE_ERROR
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchangeImpl.Companion.EMPTY_MSG
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchangeResult
import uk.gov.onelogin.features.login.domain.validateWalletStoreId.ValidateWalletStoreId
import java.util.stream.Stream
import kotlin.test.assertEquals

@Suppress("LargeClass")
class RefreshExchangeImplTest {
    private lateinit var fragmentContext: FragmentActivity
    private lateinit var context: Context
    private lateinit var getPersistentId: GetPersistentId
    private lateinit var isRefreshTokenExpired: IsTokenExpired
    private lateinit var httpClient: GenericHttpClient
    private lateinit var appIntegrity: AppIntegrity
    private lateinit var dPoPManager: DemonstratingProofOfPossessionManager
    private lateinit var getFromEncryptedSecureStore: GetFromEncryptedSecureStore
    private lateinit var saveTokenExpiry: SaveTokenExpiry
    private lateinit var tokenRepository: TokenRepository
    private lateinit var saveTokens: SaveTokens
    private lateinit var logger: MemorisedLogger
    private lateinit var timeProvider: SystemTimeProvider
    private lateinit var sut: RefreshExchange

    private lateinit var validateWalletStoreId: ValidateWalletStoreId

    @BeforeEach
    fun setup() {
        fragmentContext = mock()
        context = mock()
        getPersistentId = mock()
        isRefreshTokenExpired = mock()
        httpClient = mock()
        appIntegrity = mock()
        dPoPManager = mock()
        getFromEncryptedSecureStore = mock()
        saveTokenExpiry = mock()
        tokenRepository = mock()
        saveTokens = mock()
        logger = MemorisedLogger()
        timeProvider = mock()
        validateWalletStoreId = mock()
        sut =
            RefreshExchangeImpl(
                context = context,
                getPersistentId = getPersistentId,
                isRefreshTokenExpired = isRefreshTokenExpired,
                httpClient = httpClient,
                appIntegrity = appIntegrity,
                dPoPManager = dPoPManager,
                getFromEncryptedSecureStore = getFromEncryptedSecureStore,
                saveTokenExpiry = saveTokenExpiry,
                tokenRepository = tokenRepository,
                saveTokens = saveTokens,
                logger = logger,
                timeProvider = timeProvider,
                validateWalletStoreId = validateWalletStoreId
            )

        whenever(context.getString(any(), anyVararg()))
            .thenReturn("https://test/test")
        whenever(context.getString(any()))
            .thenReturn("test")

        runTest {
            mockSetupAndGetTokens()
            mockClientAttestation()
            mockEncryptedSecureStore("testRefreshToken", "testIdToken")
            mockDPoPAndPoP()
            mockSuccessfulHttpResponse()
            whenever(timeProvider.calculateExpiryTime(any())).thenReturn(100)
        }
    }

    @Test
    fun `successful refresh exchange`() =
        runTest {
            val result = getTokens()

            assertThat(logger, hasSize(0))
            assertEquals(RefreshExchangeResult.Success, result)
            verify(saveTokenExpiry, times(2)).saveExp(anyVararg())
            verify(tokenRepository).setTokenResponse(
                LoginTokens(
                    tokenType = "Bearer",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 100,
                    idToken = "testIdToken",
                )
            )
        }

    @Test
    fun `persistent session ID is null`() =
        runTest {
            whenever(getPersistentId()).thenReturn(null)

            val result = getTokens()

            assertEquals(RefreshExchangeResult.FirstTimeUser, result)
            assertThat(logger, hasSize(0))
        }

    @Test
    fun `persistent session ID is empty`() =
        runTest {
            whenever(getPersistentId()).thenReturn("")

            val result = getTokens()

            assertEquals(RefreshExchangeResult.FirstTimeUser, result)
            assertThat(logger, hasSize(0))
            verifyNoDownstreamInteractions()
        }

    @Test
    fun `refresh token is expired`() =
        runTest {
            whenever(isRefreshTokenExpired()).thenReturn(true)

            val result = getTokens()

            assertEquals(RefreshExchangeResult.ReauthRequired, result)
            assertThat(logger, hasSize(0))
        }

    @Test
    fun `given wallet store ID is missing, then re-auth is required`() =
        runTest {
            whenever(validateWalletStoreId()).thenReturn(false)

            val result = getTokens()

            assertEquals(RefreshExchangeResult.ReauthRequired, result)
            assertThat(logger, hasSize(0))
        }

    @Test
    fun `client attestation success with valid refresh and id tokens proceeds to refresh call`() =
        runTest {
            val result = getTokens()

            assertEquals(RefreshExchangeResult.Success, result)
            verify(dPoPManager).generateDPoP(any())
        }

    @Test
    fun `client attestation success but refresh and id tokens are null`() =
        runTest {
            mockEncryptedSecureStore("", "")

            val result = getTokens()

            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `client attestation success but refresh token is null in payload`() =
        runTest {
            mockEncryptedSecureStore(refreshToken = null, idToken = "testIdToken")

            val result = getTokens()

            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `client attestation success but id token is null in payload`() =
        runTest {
            mockEncryptedSecureStore("testRefreshToken", null)

            val result = getTokens()

            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `saved attestation is null defaults to empty client attestation`() =
        runTest {
            mockClientAttestation(AttestationResult.NotRequired(null))

            val result = getTokens()

            assertThat(logger, hasSize(0))
            assertEquals(RefreshExchangeResult.Success, result)
        }

    @Test
    fun `client attestation is expired`() =
        runTest {
            mockClientAttestation(
                AttestationResult.Failure(Exception("Client Attestation Failure!"))
            )

            val result = getTokens()

            assertEquals(RefreshExchangeResult.ClientAttestationFailure, result)
            assertThat(logger, hasSize(0))
        }

    @Test
    fun `client attestation is not required`() =
        runTest {
            mockClientAttestation(AttestationResult.NotRequired("savedAttestation"))

            val result = getTokens()

            assertThat(logger, hasSize(0))
            assertEquals(RefreshExchangeResult.Success, result)
            verify(saveTokenExpiry, times(2)).saveExp(anyVararg())
            verify(tokenRepository).setTokenResponse(
                LoginTokens(
                    tokenType = "Bearer",
                    accessToken = "accessToken",
                    accessTokenExpirationTime = 100,
                    idToken = "testIdToken"
                )
            )
        }

    @Test
    fun `client attestation not required but secure store retrieval fails`() =
        runTest {
            mockClientAttestation(AttestationResult.NotRequired("savedAttestation"))
            mockEncryptedSecureStore("", "")

            val result = getTokens()

            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `failure generating Demonstrating PoP`() =
        runTest {
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Failure("Failure"))

            val result = getTokens()

            verify(dPoPManager).generateDPoP(any())
            assertThat(
                logger,
                hasItem(
                    allOf(
                        isLogLevel(LogLevel.Error),
                        hasTag(RefreshExchangeImpl.REFRESH_ERROR_TAG),
                        hasMessage("Failure")
                    )
                )
            )
            assertEquals(RefreshExchangeResult.ReauthRequired, result)

        }

    @Test
    fun `failure generating Demonstrating PoP with error returned`() =
        runTest {
            val exp = RefreshExchangeImpl.Companion.RefreshExchangeException("error")
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Failure("Failure", exp))

            val result = getTokens()

            verify(dPoPManager).generateDPoP(any())
            assertThat(
                logger,
                hasItem(
                    allOf(
                        isLogLevel(LogLevel.Error),
                        hasTag(RefreshExchangeImpl.REFRESH_ERROR_TAG),
                        hasMessage("Failure")
                    )
                )
            )
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `failure with error msg generating app integrity PoP`() =
        runTest {
            val popError = Exception("PoP generation error")
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Success("test"))
            whenever(appIntegrity.getProofOfPossession())
                .thenReturn(SignedPoP.Failure("Failure", popError))

            val result = getTokens()

            assertThat(
                logger,
                hasItem(
                    allOf(
                        isLogLevel(LogLevel.Error),
                        hasTag(RefreshExchangeImpl.REFRESH_ERROR_TAG),
                        hasMessage("Failure"),
                        hasException(equalTo(popError))
                    )
                )
            )
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `failure without error msg generating app integrity PoP`() =
        runTest {
            mockPoPFailure()

            val result = getTokens()

            assertThat(
                logger,
                hasItem(
                    allOf(
                        isLogLevel(LogLevel.Error),
                        hasTag(RefreshExchangeImpl.REFRESH_ERROR_TAG),
                        hasMessage(ATTESTATION_POP_GENERATE_ERROR)
                    )
                )
            )
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `given http call fails, then tokens are not saved`() =
        runTest {
            mockHttpResponse(ApiResponse.Failure(status = 0, error = Exception("error")))

            getTokens()

            verifyNoInteractions(saveTokens)
        }

    @Test
    fun `network error - api response failure with message`() =
        runTest {
            mockHttpResponse(ApiResponse.Failure(status = 0, error = Exception("error")))

            val result = getTokens()

            assertThat(
                logger,
                hasItem(
                    allOf(
                        isLogLevel(LogLevel.Error),
                        hasTag(RefreshExchangeImpl.REFRESH_ERROR_TAG),
                        hasMessage("error")
                    )
                )
            )
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `network error - api response failure without message`() =
        runTest {
            mockHttpResponse(ApiResponse.Failure(status = 0, error = Exception()))

            val result = getTokens()

            assertThat(logger, hasItem(allOf(isLogLevel(LogLevel.Error), hasMessage(EMPTY_MSG))))
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `network error - makeRequest throws exception without message`() =
        runTest {
            whenever(httpClient.makeRequest(any()))
                .thenThrow(RuntimeException())

            val result = getTokens()

            assertThat(logger, hasItem(allOf(isLogLevel(LogLevel.Error), hasMessage(EMPTY_MSG))))
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `successful refresh exchange with null refresh token in response`() =
        runTest {
            mockHttpResponse(
                ApiResponse.Success(
                    "{\n" +
                        "    \"access_token\": \"accessToken\",\n" +
                        "    \"token_type\": \"Bearer\",\n" +
                        "    \"expires_in\": 1\n" +
                        "}"
                )
            )

            val result = getTokens()

            assertEquals(RefreshExchangeResult.Success, result)
            verify(saveTokenExpiry, times(1)).saveExp(anyVararg())
        }

    // This test is just to increase test coverage, the ApiResponse.Offline and ApiResponse.Loading are not used from the network package at all
    @Test
    fun `network error - api response loading`() =
        runTest {
            mockHttpResponse(ApiResponse.Offline)

            val result = getTokens()

            assertThat(logger, hasSize(0))
            assertEquals(RefreshExchangeResult.OfflineNetwork, result)
        }

    @Test
    fun `return empty refresh token`() =
        runTest {
            mockEncryptedSecureStore("", "testIdToken")

            val result = getTokens()

            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `return empty id token`() =
        runTest {
            mockEncryptedSecureStore("testRefreshToken", "")

            val result = getTokens()

            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `return null LocalAuthStatus of Success when retrieving tokens from secure store`() =
        runTest {
            mockEncryptedSecureStore(null)

            val result = getTokens()

            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @ParameterizedTest
    @MethodSource("getFromEncryptedSecureStoreErrors")
    fun `test get tokens status mapping to refresh exchange result`(
        returnedLocalAuthStatus: LocalAuthStatus,
        expected: RefreshExchangeResult
    ) = runTest {
        whenever(
            getFromEncryptedSecureStore(
                any(),
                anyVararg(),
                callback = any()
            )
        ).thenAnswer {
            (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(returnedLocalAuthStatus)
        }

        val result = getTokens()

        assertEquals(expected, result)
    }

    private suspend fun getTokens(): RefreshExchangeResult {
        lateinit var result: RefreshExchangeResult
        sut.getTokens(fragmentContext) { result = it }
        return result
    }

    private suspend fun mockSetupAndGetTokens(
        persistentId: String = "testId",
        isRefreshTokenExpired: Boolean = false,
        isWalletStoreIdValid: Boolean = true,
    ) {
        whenever(getPersistentId()).thenReturn(persistentId)
        whenever(isRefreshTokenExpired()).thenReturn(isRefreshTokenExpired)
        whenever(validateWalletStoreId()).thenReturn(isWalletStoreIdValid)
    }

    private suspend fun mockClientAttestation(
        attestationResult: AttestationResult = AttestationResult.Success("clientAttestation"),
    ) {
        whenever(appIntegrity.getClientAttestation()).thenReturn(attestationResult)
    }

    private suspend fun mockEncryptedSecureStore(
        refreshToken: String? = "",
        idToken: String? = ""
    ) {
        whenever(
            getFromEncryptedSecureStore(
                any(),
                anyVararg(),
                callback = any()
            )
        ).thenAnswer {
            (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(
                LocalAuthStatus.Success(
                    mapOf(
                        AuthTokenStoreKeys.REFRESH_TOKEN_KEY to refreshToken,
                        AuthTokenStoreKeys.ID_TOKEN_KEY to idToken
                    )
                )
            )
        }
    }

    private fun mockDPoPAndPoP() {
        whenever(dPoPManager.generateDPoP(any()))
            .thenReturn(SignedDPoP.Success("signedDPoP"))
        whenever(appIntegrity.getProofOfPossession())
            .thenReturn(SignedPoP.Success("signedPoP"))
    }

    private fun mockPoPFailure(
        message: String = "Failure",
        error: Exception? = null
    ) {
        whenever(appIntegrity.getProofOfPossession())
            .thenReturn(if (error != null) SignedPoP.Failure(message, error) else SignedPoP.Failure(message))
    }

    private suspend fun mockHttpResponse(response: ApiResponse) {
        whenever(httpClient.makeRequest(any()))
            .thenReturn(response)
    }

    private suspend fun mockSuccessfulHttpResponse() {
        mockHttpResponse(
            ApiResponse.Success(
                "{\n" +
                    "    \"access_token\": \"accessToken\",\n" +
                    "    \"refresh_token\": \"refreshToken\",\n" +
                    "    \"token_type\": \"Bearer\",\n" +
                    "    \"expires_in\": 1\n" +
                    "}"
            )
        )
    }

    private fun verifyNoDownstreamInteractions() {
        verifyNoInteractions(
            appIntegrity,
            dPoPManager,
            getFromEncryptedSecureStore,
            httpClient,
            saveTokens,
            saveTokenExpiry,
            tokenRepository,
            isRefreshTokenExpired,
            httpClient,

        )
    }

    companion object {
        @JvmStatic
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
                )
            )
    }
}
