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
import uk.gov.android.network.client.v2.GenericHttpResponse
import uk.gov.android.network.client.v2.GenericResponseException
import uk.gov.android.network.client.v2.StubHttpClient
import uk.gov.android.network.service.DefaultNetworkService
import uk.gov.android.network.service.NetworkService
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
    private val stubHttpClient = StubHttpClient()
    private val networkService: NetworkService = DefaultNetworkService(stubHttpClient)
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
                networkService = networkService,
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
    }

    @Test
    fun `successful refresh exchange`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success("clientAttestation"))
            whenever(timeProvider.calculateExpiryTime(any())).thenReturn(100)
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Success("signedDPoP"))
            whenever(appIntegrity.getProofOfPossession())
                .thenReturn(SignedPoP.Success("signedPoP"))
            whenever(validateWalletStoreId.invoke())
                .thenReturn(true)
            stubHttpClient.response = GenericHttpResponse(
                200,
                "{\n" +
                    "    \"access_token\": \"accessToken\",\n" +
                    "    \"refresh_token\": \"refreshToken\",\n" +
                    "    \"token_type\": \"Bearer\",\n" +
                    "    \"expires_in\": 1\n" +
                    "}"
            )
            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

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
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn(null)

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            assertEquals(RefreshExchangeResult.FirstTimeUser, result)
            verifyNoInteractions(isRefreshTokenExpired)
            verifyNoInteractions(appIntegrity)
            verifyNoInteractions(dPoPManager)
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            verifyNoInteractions(getFromEncryptedSecureStore)
            assertThat(logger, hasSize(0))
        }

    @Test
    fun `persistent session ID is empty`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("")

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            assertEquals(RefreshExchangeResult.FirstTimeUser, result)
            verifyNoInteractions(isRefreshTokenExpired)
            verifyNoInteractions(appIntegrity)
            verifyNoInteractions(dPoPManager)
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            verifyNoInteractions(getFromEncryptedSecureStore)
            assertThat(logger, hasSize(0))
        }

    @Test
    fun `refresh token is expired`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(isRefreshTokenExpired()).thenReturn(true)

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            assertEquals(RefreshExchangeResult.ReauthRequired, result)
            verify(isRefreshTokenExpired).invoke()
            verifyNoInteractions(appIntegrity)
            verifyNoInteractions(dPoPManager)
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            verifyNoInteractions(getFromEncryptedSecureStore)
            assertThat(logger, hasSize(0))
        }

    @Test
    fun `wallet store id validation fails`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(validateWalletStoreId.invoke()).thenReturn(false)

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            assertEquals(RefreshExchangeResult.ReauthRequired, result)
            verify(isRefreshTokenExpired).invoke()
            verify(validateWalletStoreId).invoke()
            verifyNoInteractions(appIntegrity)
            verifyNoInteractions(dPoPManager)
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            verifyNoInteractions(getFromEncryptedSecureStore)
            assertThat(logger, hasSize(0))
        }

    @Test
    fun `client attestation success with valid refresh and id tokens proceeds to refresh call`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success("clientAttestation"))
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Success("signedDPoP"))
            whenever(appIntegrity.getProofOfPossession())
                .thenReturn(SignedPoP.Success("signedPoP"))
            whenever(timeProvider.calculateExpiryTime(any())).thenReturn(100)
            stubHttpClient.response = GenericHttpResponse(
                200,
                "{\n" +
                    "    \"access_token\": \"accessToken\",\n" +
                    "    \"refresh_token\": \"refreshToken\",\n" +
                    "    \"token_type\": \"Bearer\",\n" +
                    "    \"expires_in\": 1\n" +
                    "}"
            )

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            assertEquals(RefreshExchangeResult.Success, result)
            verify(dPoPManager).generateDPoP(any())
        }

    @Test
    fun `client attestation success but refresh and id tokens are null`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success("clientAttestation"))
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to ""
                        )
                    )
                )
            }

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            assertEquals(RefreshExchangeResult.ReauthRequired, result)
            verifyNoInteractions(dPoPManager)
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
        }

    @Test
    fun `client attestation success but refresh token is null in payload`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success("clientAttestation"))
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
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            assertEquals(RefreshExchangeResult.ReauthRequired, result)
            verifyNoInteractions(dPoPManager)
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
        }

    @Test
    fun `client attestation success but id token is null in payload`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success("clientAttestation"))
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken"
                        )
                    )
                )
            }

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            assertEquals(RefreshExchangeResult.ReauthRequired, result)
            verifyNoInteractions(dPoPManager)
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
        }

    @Test
    fun `saved attestation is null defaults to empty client attestation`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.NotRequired(null))
            whenever(timeProvider.calculateExpiryTime(any())).thenReturn(100)
            whenever(validateWalletStoreId.invoke())
                .thenReturn(true)
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Success("signedDPoP"))
            whenever(appIntegrity.getProofOfPossession())
                .thenReturn(SignedPoP.Success("signedPoP"))
            stubHttpClient.response = GenericHttpResponse(
                200,
                "{\n" +
                    "    \"access_token\": \"accessToken\",\n" +
                    "    \"refresh_token\": \"refreshToken\",\n" +
                    "    \"token_type\": \"Bearer\",\n" +
                    "    \"expires_in\": 1\n" +
                    "}"
            )

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            assertThat(logger, hasSize(0))
            assertEquals(RefreshExchangeResult.Success, result)
            verify(saveTokenExpiry, times(2)).saveExp(anyVararg())
        }

    @Test
    fun `client attestation is expired`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Failure(Exception("Client Attestation failure!")))

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            assertEquals(RefreshExchangeResult.ClientAttestationFailure, result)
            verify(isRefreshTokenExpired).invoke()
            verify(appIntegrity).getClientAttestation()
            verifyNoInteractions(dPoPManager)
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            verifyNoInteractions(getFromEncryptedSecureStore)
            assertThat(logger, hasSize(0))
        }

    @Test
    fun `client attestation is not required`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.NotRequired("savedAttestation"))
            whenever(timeProvider.calculateExpiryTime(any())).thenReturn(100)
            whenever(validateWalletStoreId.invoke())
                .thenReturn(true)
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Success("signedDPoP"))
            whenever(appIntegrity.getProofOfPossession())
                .thenReturn(SignedPoP.Success("signedPoP"))
            stubHttpClient.response = GenericHttpResponse(
                200,
                "{\n" +
                    "    \"access_token\": \"accessToken\",\n" +
                    "    \"refresh_token\": \"refreshToken\",\n" +
                    "    \"token_type\": \"Bearer\",\n" +
                    "    \"expires_in\": 1\n" +
                    "}"
            )

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

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
    fun `failure generating Demonstrating PoP`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success("attestation"))
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Failure("Failure"))

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

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
            verify(isRefreshTokenExpired).invoke()
            verify(appIntegrity).getProofOfPossession()
            verify(dPoPManager).generateDPoP(any())
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `failure generating Demonstrating PoP with error returned`() =
        runTest {
            val exp = RefreshExchangeImpl.Companion.RefreshExchangeException("error")
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success("attestation"))
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(
                    SignedDPoP
                        .Failure("Failure", exp)
                )

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

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
            verify(isRefreshTokenExpired).invoke()
            verify(appIntegrity).getProofOfPossession()
            verify(dPoPManager).generateDPoP(any())
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `failure with error msg generating app integrity PoP`() =
        runTest {
            val popError = Exception("PoP generation error")
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success("savedAttestation"))
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Success("test"))
            whenever(appIntegrity.getProofOfPossession())
                .thenReturn(SignedPoP.Failure("Failure", popError))

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

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
            verify(isRefreshTokenExpired).invoke()
            verify(appIntegrity).getClientAttestation()
            verify(dPoPManager).generateDPoP(any())
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `failure without error msg generating app integrity PoP`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success("savedAttestation"))
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Success("test"))
            whenever(appIntegrity.getProofOfPossession())
                .thenReturn(SignedPoP.Failure("Failure"))

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

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
            verify(isRefreshTokenExpired).invoke()
            verify(appIntegrity).getClientAttestation()
            verify(dPoPManager).generateDPoP(any())
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `network error - api response failure with message`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.NotRequired("savedAttestation"))
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Success("signedDPoP"))
            whenever(appIntegrity.getProofOfPossession())
                .thenReturn(SignedPoP.Success("signedPoP"))
            stubHttpClient.exception = GenericResponseException(
                GenericHttpResponse(0, "error"),
                IllegalStateException("error")
            )

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            assertThat(
                logger,
                hasItem(
                    allOf(
                        isLogLevel(LogLevel.Error),
                        hasTag(RefreshExchangeImpl.REFRESH_ERROR_TAG),
                        hasMessage("API responded with 0")
                    )
                )
            )
            verify(isRefreshTokenExpired).invoke()
            verify(appIntegrity).getClientAttestation()
            verify(dPoPManager).generateDPoP(any())
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `network error - api response failure without message`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.NotRequired("savedAttestation"))
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Success("signedDPoP"))
            whenever(appIntegrity.getProofOfPossession())
                .thenReturn(SignedPoP.Success("signedPoP"))
            stubHttpClient.exception = GenericResponseException(
                GenericHttpResponse(0, ""),
                IllegalStateException()
            )

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            assertThat(logger, hasItem(allOf(isLogLevel(LogLevel.Error), hasMessage("API responded with 0"))))
            verify(isRefreshTokenExpired).invoke()
            verify(appIntegrity).getClientAttestation()
            verify(dPoPManager).generateDPoP(any())
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `network error - makeRequest throws exception without message`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success("clientAttestation"))
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Success("signedDPoP"))
            whenever(appIntegrity.getProofOfPossession())
                .thenReturn(SignedPoP.Success("signedPoP"))
            stubHttpClient.exception = RuntimeException()

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            assertThat(logger, hasItem(allOf(isLogLevel(LogLevel.Error), hasMessage(EMPTY_MSG))))
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `successful refresh exchange with null refresh token in response`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success("clientAttestation"))
            whenever(timeProvider.calculateExpiryTime(any())).thenReturn(100)
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Success("signedDPoP"))
            whenever(appIntegrity.getProofOfPossession())
                .thenReturn(SignedPoP.Success("signedPoP"))
            stubHttpClient.response = GenericHttpResponse(
                200,
                "{\n" +
                    "    \"access_token\": \"accessToken\",\n" +
                    "    \"token_type\": \"Bearer\",\n" +
                    "    \"expires_in\": 1\n" +
                    "}"
            )

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            assertEquals(RefreshExchangeResult.Success, result)
            verify(saveTokenExpiry, times(1)).saveExp(anyVararg())
        }

    // In v2, transport failures are represented as ApiResponse.Failure with TransportException
    @Test
    fun `network error - transport failure`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.Success("savedAttestation"))
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testRefreshToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }
            whenever(dPoPManager.generateDPoP(any()))
                .thenReturn(SignedDPoP.Success("signedDPoP"))
            whenever(appIntegrity.getProofOfPossession())
                .thenReturn(SignedPoP.Success("signedPoP"))
            stubHttpClient.exception = kotlinx.io.IOException()

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            verify(isRefreshTokenExpired).invoke()
            verify(appIntegrity).getClientAttestation()
            verify(dPoPManager).generateDPoP(any())
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `return empty refresh token`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.NotRequired("savedAttestation"))
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to "testIdToken"
                        )
                    )
                )
            }

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            verify(isRefreshTokenExpired).invoke()
            verify(appIntegrity).getClientAttestation()
            verifyNoInteractions(dPoPManager)
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `return empty id token`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.NotRequired("savedAttestation"))
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
                            AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "testIdToken",
                            AuthTokenStoreKeys.ID_TOKEN_KEY to ""
                        )
                    )
                )
            }

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            verify(isRefreshTokenExpired).invoke()
            verify(appIntegrity).getClientAttestation()
            verifyNoInteractions(dPoPManager)
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @Test
    fun `return null LocalAuthStatus of Success when retrieving tokens from secure store`() =
        runTest {
            lateinit var result: RefreshExchangeResult
            whenever(getPersistentId()).thenReturn("testId")
            whenever(validateWalletStoreId.invoke()).thenReturn(true)
            whenever(isRefreshTokenExpired()).thenReturn(false)
            whenever(appIntegrity.getClientAttestation())
                .thenReturn(AttestationResult.NotRequired("savedAttestation"))
            whenever(
                getFromEncryptedSecureStore(
                    any(),
                    anyVararg(),
                    callback = any()
                )
            ).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.Success(null)
                )
            }

            sut.getTokens(
                fragmentContext,
                handleResult = {
                    result = it
                }
            )

            verify(isRefreshTokenExpired).invoke()
            verify(appIntegrity).getClientAttestation()
            verifyNoInteractions(dPoPManager)
            verifyNoInteractions(saveTokenExpiry)
            verifyNoInteractions(tokenRepository)
            verifyNoInteractions(saveTokens)
            assertEquals(RefreshExchangeResult.ReauthRequired, result)
        }

    @ParameterizedTest
    @MethodSource("getFromEncryptedSecureStoreErrors")
    fun `test get tokens status mapping to refresh exchange result`(
        returnedLocalAuthStatus: LocalAuthStatus,
        expected: RefreshExchangeResult
    ) = runTest {
        lateinit var result: RefreshExchangeResult
        whenever(getPersistentId()).thenReturn("testId")
        whenever(validateWalletStoreId.invoke()).thenReturn(true)
        whenever(isRefreshTokenExpired()).thenReturn(false)
        whenever(appIntegrity.getClientAttestation())
            .thenReturn(AttestationResult.NotRequired("savedAttestation"))
        whenever(
            getFromEncryptedSecureStore(
                any(),
                anyVararg(),
                callback = any()
            )
        ).thenAnswer {
            (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(returnedLocalAuthStatus)
        }

        sut.getTokens(
            fragmentContext,
            handleResult = {
                result = it
            }
        )

        assertEquals(expected, result)
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
