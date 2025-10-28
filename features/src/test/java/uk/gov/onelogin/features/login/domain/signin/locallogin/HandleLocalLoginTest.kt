package uk.gov.onelogin.features.login.domain.signin.locallogin

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.tokens.domain.retrieve.GetFromEncryptedSecureStore
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.core.utils.MockitoHelper

class HandleLocalLoginTest {
    private val mockActivity: FragmentActivity = mock()
    private val mockGetRefreshTokenExpiry: GetTokenExpiry = mock()
    private val mockGetAccessTokenExpiry: GetTokenExpiry = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockGetFromEncryptedSecureStore: GetFromEncryptedSecureStore = mock()
    private val mockBioPrefHandler: LocalAuthManager = mock()
    private val mockIsAccessTokenExpired: IsTokenExpired = mock()
    private val mockIsRefreshTokenExpired: IsTokenExpired = mock()

    private lateinit var useCase: HandleLocalLogin

    @BeforeEach
    fun setup() {
        useCase = HandleLocalLoginImpl(
            mockGetAccessTokenExpiry,
            mockGetRefreshTokenExpiry,
            mockTokenRepository,
            mockIsAccessTokenExpired,
            mockIsRefreshTokenExpired,
            mockGetFromEncryptedSecureStore,
            mockBioPrefHandler
        )
    }

    @Test
    fun accessTokenExpiredAndNotNull_reAuthLogin() {
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(true)
        whenever(mockGetAccessTokenExpiry.invoke()).thenReturn(1)
        whenever(mockBioPrefHandler.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(false))

        runBlocking {
            useCase(
                mockActivity
            ) {
                assertEquals(LocalAuthStatus.ReAuthSignIn, it)
            }
        }
    }

    @Test
    fun refreshTokenExpiredAndNotNull_reAuthLogin() {
        whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(true)
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(1)
        whenever(mockBioPrefHandler.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(false))

        runBlocking {
            useCase(
                mockActivity
            ) {
                assertEquals(LocalAuthStatus.ReAuthSignIn, it)
            }
        }
    }

    @Test
    fun accessTokenExpiredAndNull_refreshLogin() {
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(true)
        whenever(mockGetAccessTokenExpiry.invoke()).thenReturn(null)
        whenever(mockBioPrefHandler.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(false))

        runBlocking {
            useCase(
                mockActivity
            ) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }
        }
    }

    @Test
    fun refreshTokenNull_fallBackToAccessTokenFlow() {
        val expectedResult = LocalAuthStatus.Success(
            payload = mapOf(
                AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "accessToken",
                AuthTokenStoreKeys.ID_TOKEN_KEY to "idToken"
            )
        )
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
        whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(true)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockBioPrefHandler.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(false))
        wheneverBlocking {
            mockGetFromEncryptedSecureStore.invoke(
                context = any(),
                ArgumentMatchers.contains(AuthTokenStoreKeys.ID_TOKEN_KEY),
                callback = any()
            )
        }.thenAnswer {
            (it.arguments[3] as (LocalAuthStatus) -> Unit).invoke(expectedResult)
        }

        runBlocking {
            useCase(
                mockActivity
            ) {
                assertEquals(expectedResult, it)
            }
        }
    }

    @Test
    fun accessToken_idTokenNull_refreshLogin() {
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockGetAccessTokenExpiry.invoke()).thenReturn(unexpiredTime)
        whenever(mockBioPrefHandler.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(false))
        wheneverBlocking {
            mockGetFromEncryptedSecureStore.invoke(
                context = any(),
                ArgumentMatchers.contains(AuthTokenStoreKeys.ID_TOKEN_KEY),
                callback = any()
            )
        }.thenAnswer {
            (it.arguments[3] as (LocalAuthStatus) -> Unit).invoke(
                LocalAuthStatus.Success(
                    payload =
                    mapOf(
                        AuthTokenStoreKeys.ACCESS_TOKEN_KEY to "accessToken"
                    )
                )
            )
        }

        runBlocking {
            useCase(mockActivity) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }
        }
    }

    @Test
    fun refreshToken_idTokenNull_refreshLogin() {
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(1)
        whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(false)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockGetAccessTokenExpiry.invoke()).thenReturn(unexpiredTime)
        whenever(mockBioPrefHandler.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(false))
        wheneverBlocking {
            mockGetFromEncryptedSecureStore.invoke(
                context = any(),
                ArgumentMatchers.contains(AuthTokenStoreKeys.ID_TOKEN_KEY),
                callback = any()
            )
        }.thenAnswer {
            (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(
                LocalAuthStatus.Success(
                    payload =
                    mapOf(
                        AuthTokenStoreKeys.ACCESS_TOKEN_KEY to "accessToken",
                        AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "refreshToken"
                    )
                )
            )
        }

        runBlocking {
            useCase(mockActivity) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }
        }
    }

    @Test
    fun refreshToken_accessTokenNull_refreshLogin() {
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(1)
        whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(false)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockGetAccessTokenExpiry.invoke()).thenReturn(unexpiredTime)
        whenever(mockBioPrefHandler.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(false))
        wheneverBlocking {
            mockGetFromEncryptedSecureStore.invoke(
                context = any(),
                ArgumentMatchers.contains(AuthTokenStoreKeys.ACCESS_TOKEN_KEY),
                callback = any()
            )
        }.thenAnswer {
            (it.arguments[3] as (LocalAuthStatus) -> Unit).invoke(
                LocalAuthStatus.Success(
                    payload =
                    mapOf(
                        AuthTokenStoreKeys.ID_TOKEN_KEY to "idToken",
                        AuthTokenStoreKeys.REFRESH_TOKEN_KEY to "refreshToken"
                    )
                )
            )
        }

        runBlocking {
            useCase(mockActivity) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }
        }
    }

    @Test
    fun refreshToken_refreshTokenNull_refreshLogin() {
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(1)
        whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(false)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockGetAccessTokenExpiry.invoke()).thenReturn(unexpiredTime)
        whenever(mockBioPrefHandler.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(false))
        wheneverBlocking {
            mockGetFromEncryptedSecureStore.invoke(
                context = any(),
                ArgumentMatchers.contains(AuthTokenStoreKeys.REFRESH_TOKEN_KEY),
                callback = any()
            )
        }.thenAnswer {
            (it.arguments[3] as (LocalAuthStatus) -> Unit).invoke(
                LocalAuthStatus.Success(
                    payload =
                    mapOf(
                        AuthTokenStoreKeys.ACCESS_TOKEN_KEY to "accessToken",
                        AuthTokenStoreKeys.ID_TOKEN_KEY to "idToken"
                    )
                )
            )
        }

        runBlocking {
            useCase(mockActivity) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }
        }
    }

    @Test
    fun accessToken_bioPrefNone_refreshLogin_biometricsNone() {
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockGetAccessTokenExpiry()).thenReturn(null)
        whenever(mockBioPrefHandler.localAuthPreference).thenReturn(LocalAuthPreference.Disabled)

        runBlocking {
            useCase(mockActivity) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }
        }
    }

    @Test
    fun refreshToken_bioPrefNone_refreshLogin_biometricsNone() {
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(1)
        whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(false)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockGetAccessTokenExpiry()).thenReturn(null)
        whenever(mockBioPrefHandler.localAuthPreference).thenReturn(LocalAuthPreference.Disabled)

        runBlocking {
            useCase(mockActivity) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }
        }
    }

    @Test
    fun accessToken_bioPrefNone_refreshLogin_biometricsNull() {
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockGetAccessTokenExpiry()).thenReturn(null)
        whenever(mockBioPrefHandler.localAuthPreference).thenReturn(LocalAuthPreference.Disabled)

        runBlocking {
            useCase(mockActivity) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }
        }
    }

    @Test
    fun refreshToken_bioPrefNone_refreshLogin_biometricsNull() {
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(1)
        whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(false)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockGetAccessTokenExpiry()).thenReturn(null)
        whenever(mockBioPrefHandler.localAuthPreference).thenReturn(LocalAuthPreference.Disabled)

        runBlocking {
            useCase(mockActivity) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }
        }
    }

    @Test
    fun accessTokenNonSuccessResponseFromGetFromSecureStore() {
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockBioPrefHandler.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(false))

        runBlocking {
            whenever(mockGetFromEncryptedSecureStore(any(), any(), callback = any())).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.ManualSignIn)
            }

            useCase(mockActivity) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }

            verify(mockTokenRepository, times(0)).setTokenResponse(any())
        }
    }

    @Test
    fun refreshTokenNonSuccessResponseFromGetFromSecureStore() {
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
        whenever(mockIsRefreshTokenExpired.invoke()).thenReturn(false)
        whenever(mockBioPrefHandler.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(false))

        runBlocking {
            whenever(mockGetFromEncryptedSecureStore(any(), any(), callback = any())).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.ManualSignIn)
            }

            useCase(mockActivity) {
                assertEquals(LocalAuthStatus.ManualSignIn, it)
            }

            verify(mockTokenRepository, times(0)).setTokenResponse(any())
        }
    }

    @Test
    fun accessToken_goodLogin() {
        val accessToken = "Token"
        val idToken = "IdToken"
        val tokenResponse =
            mapOf(
                AuthTokenStoreKeys.ACCESS_TOKEN_KEY to accessToken,
                AuthTokenStoreKeys.ID_TOKEN_KEY to idToken
            )

        whenever(mockGetAccessTokenExpiry()).thenReturn(unexpiredTime)
        whenever(mockGetRefreshTokenExpiry.invoke()).thenReturn(null)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockBioPrefHandler.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(false))

        runBlocking {
            whenever(
                mockGetFromEncryptedSecureStore(
                    context = any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            ).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.Success(tokenResponse)
                )
            }

            useCase(mockActivity) {
                assertEquals(LocalAuthStatus.Success(tokenResponse), it)
            }

            verify(mockTokenRepository).setTokenResponse(
                TokenResponse(
                    accessToken = accessToken,
                    idToken = idToken,
                    tokenType = "",
                    accessTokenExpirationTime = unexpiredTime
                )
            )
        }
    }

    @Test
    fun refreshToken_goodLogin() {
        val accessToken = "Token"
        val idToken = "IdToken"
        val refreshToken = "RefreshToken"
        val tokenResponse =
            mapOf(
                AuthTokenStoreKeys.ACCESS_TOKEN_KEY to accessToken,
                AuthTokenStoreKeys.ID_TOKEN_KEY to idToken,
                AuthTokenStoreKeys.REFRESH_TOKEN_KEY to refreshToken
            )

        whenever(mockGetAccessTokenExpiry()).thenReturn(unexpiredTime)
        whenever(mockIsAccessTokenExpired.invoke()).thenReturn(false)
        whenever(mockBioPrefHandler.localAuthPreference)
            .thenReturn(LocalAuthPreference.Enabled(false))

        runBlocking {
            whenever(
                mockGetFromEncryptedSecureStore(
                    context = any(),
                    MockitoHelper.anyObject(),
                    callback = any()
                )
            ).thenAnswer {
                (it.arguments[2] as (LocalAuthStatus) -> Unit).invoke(
                    LocalAuthStatus.Success(tokenResponse)
                )
            }

            useCase(mockActivity) {
                assertEquals(LocalAuthStatus.Success(tokenResponse), it)
            }

            verify(mockTokenRepository).setTokenResponse(
                TokenResponse(
                    accessToken = accessToken,
                    idToken = idToken,
                    tokenType = "",
                    accessTokenExpirationTime = unexpiredTime
                )
            )
        }
    }

    companion object {
        val unexpiredTime = System.currentTimeMillis() + 100000L
    }
}
