package uk.gov.onelogin.features.login.domain.refresh

import android.content.Context
import androidx.fragment.app.FragmentActivity
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyVararg
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.authentication.login.refresh.DemonstratingProofOfPossessionManager
import uk.gov.android.authentication.login.refresh.SignedDPoP
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.tokens.domain.retrieve.GetFromEncryptedSecureStore
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.core.utils.SystemTimeProvider
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationResult

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
    private lateinit var logger: Logger
    private lateinit var timeProvider: SystemTimeProvider
    private lateinit var sut: RefreshExchange

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
        logger = mock()
        timeProvider = mock()
        sut = RefreshExchangeImpl(
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
            timeProvider = timeProvider
        )

        whenever(context.getString(any(), anyVararg()))
            .thenReturn("https://test/test")
        whenever(context.getString(any()))
            .thenReturn("test")
    }

    @Test
    fun `successful refresh exchange`() = runTest {
        lateinit var result: LocalAuthStatus
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
        whenever(httpClient.makeRequest(any()))
            .thenReturn(
                ApiResponse.Success(
                    "{\n" +
                        "    \"access_token\": \"accessToken\",\n" +
                        "    \"refresh_token\": \"refreshToken\",\n" +
                        "    \"token_type\": \"Bearer\",\n" +
                        "    \"expires_in\": 1\n" +
                        "}"
                )
            )

        sut.getTokens(
            fragmentContext,
            handleResult = {
                result = it
            }
        )

        verifyNoInteractions(logger)
        assertEquals(LocalAuthStatus.Success(mapOf()), result)
        verify(saveTokenExpiry, times(2)).saveExp(any())
        verify(tokenRepository).setTokenResponse(
            TokenResponse(
                tokenType = "Bearer",
                accessToken = "accessToken",
                accessTokenExpirationTime = 100,
                idToken = "testIdToken",
                refreshToken = ""
            )
        )
    }

    @Test
    fun `persistent session ID is null`() = runTest {
        lateinit var result: LocalAuthStatus
        whenever(getPersistentId()).thenReturn(null)

        sut.getTokens(
            fragmentContext,
            handleResult = {
                result = it
            }
        )

        assertEquals(LocalAuthStatus.ManualSignIn, result)
        verifyNoInteractions(isRefreshTokenExpired)
        verifyNoInteractions(httpClient)
        verifyNoInteractions(appIntegrity)
        verifyNoInteractions(dPoPManager)
        verifyNoInteractions(saveTokenExpiry)
        verifyNoInteractions(tokenRepository)
        verifyNoInteractions(saveTokens)
        verifyNoInteractions(getFromEncryptedSecureStore)
        verifyNoInteractions(logger)
    }

    @Test
    fun `persistent session ID is empty`() = runTest {
        lateinit var result: LocalAuthStatus
        whenever(getPersistentId()).thenReturn(null)

        sut.getTokens(
            fragmentContext,
            handleResult = {
                result = it
            }
        )

        assertEquals(LocalAuthStatus.ManualSignIn, result)
        verifyNoInteractions(isRefreshTokenExpired)
        verifyNoInteractions(httpClient)
        verifyNoInteractions(appIntegrity)
        verifyNoInteractions(dPoPManager)
        verifyNoInteractions(saveTokenExpiry)
        verifyNoInteractions(tokenRepository)
        verifyNoInteractions(saveTokens)
        verifyNoInteractions(getFromEncryptedSecureStore)
        verifyNoInteractions(logger)
    }

    @Test
    fun `refresh token is expired`() = runTest {
        lateinit var result: LocalAuthStatus
        whenever(getPersistentId()).thenReturn("testId")
        whenever(isRefreshTokenExpired()).thenReturn(true)

        sut.getTokens(
            fragmentContext,
            handleResult = {
                result = it
            }
        )

        assertEquals(LocalAuthStatus.ReAuthSignIn, result)
        verify(isRefreshTokenExpired).invoke()
        verifyNoInteractions(httpClient)
        verifyNoInteractions(appIntegrity)
        verifyNoInteractions(dPoPManager)
        verifyNoInteractions(saveTokenExpiry)
        verifyNoInteractions(tokenRepository)
        verifyNoInteractions(saveTokens)
        verifyNoInteractions(getFromEncryptedSecureStore)
        verifyNoInteractions(logger)
    }

    @Test
    fun `client attestation is expired`() = runTest {
        lateinit var result: LocalAuthStatus
        whenever(getPersistentId()).thenReturn("testId")
        whenever(isRefreshTokenExpired()).thenReturn(false)
        whenever(appIntegrity.getClientAttestation())
            .thenReturn(AttestationResult.Failure("Client Attestation failure!"))

        sut.getTokens(
            fragmentContext,
            handleResult = {
                result = it
            }
        )

        assertEquals(LocalAuthStatus.ClientAttestationFailure, result)
        verify(isRefreshTokenExpired).invoke()
        verify(appIntegrity).getClientAttestation()
        verifyNoInteractions(httpClient)
        verifyNoInteractions(dPoPManager)
        verifyNoInteractions(saveTokenExpiry)
        verifyNoInteractions(tokenRepository)
        verifyNoInteractions(saveTokens)
        verifyNoInteractions(getFromEncryptedSecureStore)
        verifyNoInteractions(logger)
    }

    @Test
    fun `client attestation is not required`() = runTest {
        lateinit var result: LocalAuthStatus
        whenever(getPersistentId()).thenReturn("testId")
        whenever(isRefreshTokenExpired()).thenReturn(false)
        whenever(appIntegrity.getClientAttestation())
            .thenReturn(AttestationResult.NotRequired("savedAttestation"))
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
        whenever(httpClient.makeRequest(any()))
            .thenReturn(
                ApiResponse.Success(
                    "{\n" +
                        "    \"access_token\": \"accessToken\",\n" +
                        "    \"refresh_token\": \"refreshToken\",\n" +
                        "    \"token_type\": \"Bearer\",\n" +
                        "    \"expires_in\": 1\n" +
                        "}"
                )
            )

        sut.getTokens(
            fragmentContext,
            handleResult = {
                result = it
            }
        )

        verifyNoInteractions(logger)
        assertEquals(LocalAuthStatus.Success(mapOf()), result)
        verify(saveTokenExpiry, times(2)).saveExp(any())
        verify(tokenRepository).setTokenResponse(
            TokenResponse(
                tokenType = "Bearer",
                accessToken = "accessToken",
                accessTokenExpirationTime = 100,
                idToken = "testIdToken",
                refreshToken = ""
            )
        )
    }

    @Test
    fun `secure store error`() = runTest {
        lateinit var result: LocalAuthStatus
        whenever(getPersistentId()).thenReturn("testId")
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
            (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.SecureStoreError)
        }

        sut.getTokens(
            fragmentContext,
            handleResult = {
                result = it
            }
        )

        verifyNoInteractions(logger)
        verify(isRefreshTokenExpired).invoke()
        verify(appIntegrity).getClientAttestation()
        verifyNoInteractions(httpClient)
        verifyNoInteractions(saveTokenExpiry)
        verifyNoInteractions(tokenRepository)
        verifyNoInteractions(saveTokens)
        assertEquals(LocalAuthStatus.SecureStoreError, result)
    }

    @Test
    fun `failure generating Demonstrating PoP`() = runTest {
        lateinit var result: LocalAuthStatus
        whenever(getPersistentId()).thenReturn("testId")
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

        verify(logger).error(
            eq(RefreshExchangeImpl.REFRESH_ERROR_TAG),
            eq("Failure"),
            any()
        )
        verify(isRefreshTokenExpired).invoke()
        verify(appIntegrity).getProofOfPossession()
        verify(dPoPManager).generateDPoP(any())
        verifyNoInteractions(httpClient)
        verifyNoInteractions(saveTokenExpiry)
        verifyNoInteractions(tokenRepository)
        verifyNoInteractions(saveTokens)
        assertEquals(LocalAuthStatus.ReAuthSignIn, result)
    }

    @Test
    fun `failure generating Demonstrating PoP with error returned`() = runTest {
        val exp = RefreshExchangeImpl.Companion.RefreshExchangeException("error")
        lateinit var result: LocalAuthStatus
        whenever(getPersistentId()).thenReturn("testId")
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

        verify(logger).error(
            eq(RefreshExchangeImpl.REFRESH_ERROR_TAG),
            eq("Failure"),
            eq(exp)
        )
        verify(isRefreshTokenExpired).invoke()
        verify(appIntegrity).getProofOfPossession()
        verify(dPoPManager).generateDPoP(any())
        verifyNoInteractions(httpClient)
        verifyNoInteractions(saveTokenExpiry)
        verifyNoInteractions(tokenRepository)
        verifyNoInteractions(saveTokens)
        assertEquals(LocalAuthStatus.ReAuthSignIn, result)
    }

    @Test
    fun `failure generating app integrity PoP`() = runTest {
        lateinit var result: LocalAuthStatus
        whenever(getPersistentId()).thenReturn("testId")
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

        verify(logger).error(
            eq(RefreshExchangeImpl.REFRESH_ERROR_TAG),
            eq("Failure"),
            any()
        )
        verify(isRefreshTokenExpired).invoke()
        verify(appIntegrity).getClientAttestation()
        verify(dPoPManager).generateDPoP(any())
        verifyNoInteractions(httpClient)
        verifyNoInteractions(saveTokenExpiry)
        verifyNoInteractions(tokenRepository)
        verifyNoInteractions(saveTokens)
        assertEquals(LocalAuthStatus.ReAuthSignIn, result)
    }

    @Test
    fun `network error - api response failure`() = runTest {
        lateinit var result: LocalAuthStatus
        whenever(getPersistentId()).thenReturn("testId")
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
        whenever(httpClient.makeRequest(any()))
            .thenReturn(
                ApiResponse.Failure(
                    status = 0,
                    error = Exception("error")
                )
            )

        sut.getTokens(
            fragmentContext,
            handleResult = {
                result = it
            }
        )

        verify(logger).error(
            eq(RefreshExchangeImpl.REFRESH_ERROR_TAG),
            eq("error"),
            any()
        )
        verify(isRefreshTokenExpired).invoke()
        verify(appIntegrity).getClientAttestation()
        verify(dPoPManager).generateDPoP(any())
        verify(httpClient).makeRequest(any())
        verifyNoInteractions(saveTokenExpiry)
        verifyNoInteractions(tokenRepository)
        verifyNoInteractions(saveTokens)
        assertEquals(LocalAuthStatus.ReAuthSignIn, result)
    }

    // This test is just to increase test coverage, the ApiResponse.Offline and ApiResponse.Loading are not used from the network package at all
    @Test
    fun `network error - api response loading`() = runTest {
        lateinit var result: LocalAuthStatus
        whenever(getPersistentId()).thenReturn("testId")
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
        whenever(httpClient.makeRequest(any()))
            .thenReturn(ApiResponse.Offline)

        sut.getTokens(
            fragmentContext,
            handleResult = {
                result = it
            }
        )

        verify(isRefreshTokenExpired).invoke()
        verify(appIntegrity).getClientAttestation()
        verify(dPoPManager).generateDPoP(any())
        verify(httpClient).makeRequest(any())
        verifyNoInteractions(logger)
        verifyNoInteractions(saveTokenExpiry)
        verifyNoInteractions(tokenRepository)
        verifyNoInteractions(saveTokens)
        assertEquals(LocalAuthStatus.ReAuthSignIn, result)
    }

    @Test
    fun `return empty refresh token`() = runTest {
        lateinit var result: LocalAuthStatus
        whenever(getPersistentId()).thenReturn("testId")
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
        verifyNoInteractions(httpClient)
        verifyNoInteractions(saveTokenExpiry)
        verifyNoInteractions(tokenRepository)
        verifyNoInteractions(saveTokens)
        assertEquals(LocalAuthStatus.ReAuthSignIn, result)
    }

    @Test
    fun `return empty id token`() = runTest {
        lateinit var result: LocalAuthStatus
        whenever(getPersistentId()).thenReturn("testId")
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
        verifyNoInteractions(httpClient)
        verifyNoInteractions(saveTokenExpiry)
        verifyNoInteractions(tokenRepository)
        verifyNoInteractions(saveTokens)
        assertEquals(LocalAuthStatus.ReAuthSignIn, result)
    }
}
