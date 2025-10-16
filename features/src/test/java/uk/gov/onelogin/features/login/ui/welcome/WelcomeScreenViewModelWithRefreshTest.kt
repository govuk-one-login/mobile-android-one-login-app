package uk.gov.onelogin.features.login.ui.welcome

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import kotlin.test.assertFalse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.login.AuthenticationError
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.featureflags.InMemoryFeatureFlags
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.LocalAuthManagerImpl
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsManager
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsStatus
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.android.localauth.preference.LocalAuthPreferenceRepository
import uk.gov.android.network.online.OnlineChecker
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.counter.Counter
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.data.MainNavRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken
import uk.gov.onelogin.core.tokens.domain.save.SavePersistentId
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.ExpiryInfo
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.login.domain.signin.loginredirect.HandleLoginRedirect
import uk.gov.onelogin.features.login.domain.signin.remotelogin.HandleRemoteLogin
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreenViewModel
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class WelcomeScreenViewModelWithRefreshTest {
    private val mockContext: Context = mock()
    private lateinit var mockFragmentActivity: FragmentActivity
    private lateinit var localAuthPreferenceRepo: LocalAuthPreferenceRepository
    private lateinit var deviceBiometricsManager: DeviceBiometricsManager
    private lateinit var analyticsLogger: AnalyticsLogger
    private lateinit var localAuthManager: LocalAuthManager
    private lateinit var mockTokenRepository: TokenRepository
    private lateinit var mockAutoInitialiseSecureStore: AutoInitialiseSecureStore
    private lateinit var mockVerifyIdToken: VerifyIdToken
    private lateinit var mockNavigator: Navigator
    private lateinit var mockFeatureFlags: FeatureFlags
    private lateinit var mockOnlineChecker: OnlineChecker
    private lateinit var mockSaveTokens: SaveTokens
    private lateinit var mockSaveTokenExpiry: SaveTokenExpiry
    private lateinit var mockHandleRemoteLogin: HandleRemoteLogin
    private lateinit var mockHandleLoginRedirect: HandleLoginRedirect
    private lateinit var mockSignOutUseCase: SignOutUseCase
    private lateinit var mockSavePersistentId: SavePersistentId
    private lateinit var mockCounter: Counter
    private val logger = SystemLogger()

    private val testAccessToken = "testAccessToken"
    private var testIdToken: String = "testIdToken"
    private val validRefreshToken = "ewogICJhbGciOiAiRVMyNTYiLAogICJ0eXAiOiAiSldUIiwKICAia2lkI" +
        "jogImFiY2QtMTIzNCIKfQ.ewogICJhdWQiOiAidGVzdCIsCiAgImV4cCI6IDE3NjMxMDg2MTcKfQ.abdcd"
    private val tokenResponse =
        TokenResponse(
            "testType",
            testAccessToken,
            1L,
            testIdToken,
            validRefreshToken
        )
    private val accessDeniedError = AuthenticationError(
        "access_denied",
        AuthenticationError.ErrorType.ACCESS_DENIED
    )
    private val oauthError = AuthenticationError(
        "oauth_error",
        AuthenticationError.ErrorType.OAUTH
    )
    private val oauthError500 = AuthenticationError(
        "oauth_error",
        AuthenticationError.ErrorType.OAUTH,
        status = 500
    )
    private val serverError = AuthenticationError(
        "server_error",
        AuthenticationError.ErrorType.SERVER_ERROR
    )
    private val tokenError400 = AuthenticationError(
        "token_error",
        AuthenticationError.ErrorType.TOKEN_ERROR
    )

    private lateinit var viewModel: WelcomeScreenViewModel

    @Test
    fun `handleIntent when data != null, device secure, no biometrics, verify id token success`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
            whenever(deviceBiometricsManager.getCredentialStatus())
                .thenReturn(DeviceBiometricsStatus.UNKNOWN)
            whenever(mockSaveTokenExpiry.extractExpFromRefreshToken(any())).thenReturn(0)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent,
                activity = mockFragmentActivity
            )

            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockSavePersistentId).invoke()
            verify(mockSaveTokenExpiry).saveExp(
                ExpiryInfo(
                    key = ACCESS_TOKEN_EXPIRY_KEY,
                    value = tokenResponse.accessTokenExpirationTime
                )
            )
            verify(localAuthPreferenceRepo)
                .setLocalAuthPref(LocalAuthPreference.Enabled(false))
            verify(mockAutoInitialiseSecureStore, times(1))
                .initialise(validRefreshToken)
            verify(mockNavigator).navigate(MainNavRoutes.Start, true)
            verify(mockCounter).reset()
        }

    @Test
    fun `when data != null, device secure, verify id token success, bio pref set to biometrics`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
            whenever(deviceBiometricsManager.getCredentialStatus())
                .thenReturn(DeviceBiometricsStatus.SUCCESS)
            whenever(localAuthPreferenceRepo.getLocalAuthPref())
                .thenReturn(LocalAuthPreference.Enabled(true))
            // Login redirect fires `onSuccess`
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            // re-authenticate is false by default
            viewModel.handleActivityResult(
                mockIntent,
                activity = mockFragmentActivity
            )

            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockSavePersistentId).invoke()
            verify(mockSaveTokenExpiry).saveExp(
                ExpiryInfo(
                    key = ACCESS_TOKEN_EXPIRY_KEY,
                    value = tokenResponse.accessTokenExpirationTime
                ),
                ExpiryInfo(
                    key = REFRESH_TOKEN_EXPIRY_KEY,
                    value = mockSaveTokenExpiry.extractExpFromRefreshToken(validRefreshToken)
                )
            )
            verify(localAuthPreferenceRepo, times(0)).setLocalAuthPref(any())
            verify(mockAutoInitialiseSecureStore, times(1))
                .initialise(validRefreshToken)
            verify(mockNavigator).navigate(MainNavRoutes.Start, true)
        }

    @Test
    fun `handleIntent when data != null and device is secure with ok biometrics`() =
        runTest {
            createMocks(isLocalAuthMocked = true)
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
            whenever(deviceBiometricsManager.getCredentialStatus())
                .thenReturn(DeviceBiometricsStatus.SUCCESS)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent,
                activity = mockFragmentActivity
            )

            verify(mockSaveTokenExpiry).saveExp(
                ExpiryInfo(
                    key = ACCESS_TOKEN_EXPIRY_KEY,
                    value = tokenResponse.accessTokenExpirationTime
                )
            )
            verify(mockSaveTokenExpiry, times(0)).saveExp(
                ExpiryInfo(
                    key = REFRESH_TOKEN_EXPIRY_KEY,
                    value = 1763108617
                )
            )
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockSavePersistentId).invoke()
            verify(localAuthPreferenceRepo, times(0)).setLocalAuthPref(any())
        }

    @Test
    fun `when data != null and device is secure with ok biometrics and pref set to none`() =
        runTest {
            createMocks(isLocalAuthMocked = true)
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
            whenever(deviceBiometricsManager.getCredentialStatus())
                .thenReturn(DeviceBiometricsStatus.SUCCESS)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)
            whenever(localAuthPreferenceRepo.getLocalAuthPref())
                .thenReturn(LocalAuthPreference.Disabled)

            viewModel.handleActivityResult(
                mockIntent,
                activity = mockFragmentActivity
            )

            verify(mockSaveTokenExpiry).saveExp(
                ExpiryInfo(
                    key = ACCESS_TOKEN_EXPIRY_KEY,
                    value = tokenResponse.accessTokenExpirationTime
                )
            )
            verify(mockSaveTokenExpiry, times(0)).saveExp(
                ExpiryInfo(
                    key = REFRESH_TOKEN_EXPIRY_KEY,
                    value = 1763108617
                )
            )
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockSavePersistentId).invoke()
            verify(localAuthPreferenceRepo, times(0)).setLocalAuthPref(any())
        }

    @Test
    fun `handleIntent when data != null and device is not secure`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(false)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent,
                activity = mockFragmentActivity
            )

            verifyNoInteractions(mockSaveTokens)
            verify(mockSaveTokenExpiry).saveExp(
                ExpiryInfo(
                    key = ACCESS_TOKEN_EXPIRY_KEY,
                    value = tokenResponse.accessTokenExpirationTime
                )
            )
            verify(mockSaveTokenExpiry, times(0)).saveExp(
                ExpiryInfo(
                    key = REFRESH_TOKEN_EXPIRY_KEY,
                    value = 1763108617
                )
            )
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockSavePersistentId).invoke()
            verify(localAuthPreferenceRepo).setLocalAuthPref(LocalAuthPreference.Disabled)
            verify(mockNavigator).navigate(MainNavRoutes.Start, true)
        }

    @Test
    fun `handleIntent when data != null, device not secure and reauth is true`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(false)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(false)
            whenever(localAuthPreferenceRepo.getLocalAuthPref())
                .thenReturn((LocalAuthPreference.Disabled))

            viewModel.handleActivityResult(
                mockIntent,
                true,
                activity = mockFragmentActivity
            )

            verify(mockSaveTokenExpiry).saveExp(
                ExpiryInfo(
                    key = ACCESS_TOKEN_EXPIRY_KEY,
                    value = tokenResponse.accessTokenExpirationTime
                )
            )
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockSavePersistentId).invoke()
            verify(mockNavigator).goBack()
            verifyNoInteractions(mockSaveTokens)
        }

    @Test
    fun `handleIntent when data != null, device is secure and reauth is true`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
            whenever(localAuthPreferenceRepo.getLocalAuthPref())
                .thenReturn((LocalAuthPreference.Enabled(false)))

            viewModel.handleActivityResult(
                mockIntent,
                true,
                activity = mockFragmentActivity
            )

            verify(mockSaveTokenExpiry).saveExp(
                ExpiryInfo(
                    key = ACCESS_TOKEN_EXPIRY_KEY,
                    value = tokenResponse.accessTokenExpirationTime
                ),
                ExpiryInfo(
                    key = REFRESH_TOKEN_EXPIRY_KEY,
                    value = mockSaveTokenExpiry.extractExpFromRefreshToken(validRefreshToken)
                )
            )
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockSavePersistentId).invoke()
            verify(mockNavigator).goBack()
            verify(mockSaveTokens).save(validRefreshToken)
        }

    @Test
    fun `handleIntent when data != null && access_denied, device is secure and reauth is true`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (error: AuthenticationError) -> Unit)
                        .invoke(accessDeniedError)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent,
                true,
                activity = mockFragmentActivity
            )

            verify(mockSignOutUseCase).invoke()
            verify(mockNavigator).navigate(SignOutRoutes.ReAuthError)
            verifyNoInteractions(mockSavePersistentId)
            assertThat("logger has log", logger.contains("access_denied"))
        }

    @Test
    fun `handleIntent when data != null && oauth_error, device is secure and reauth is true`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (error: AuthenticationError) -> Unit)
                        .invoke(oauthError)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent,
                true,
                activity = mockFragmentActivity
            )

            verifyNoInteractions(mockSignOutUseCase)
            verifyNoInteractions(mockSavePersistentId)
            verify(mockNavigator).navigate(LoginRoutes.SignInUnrecoverableError, true)
            assertThat("logger has no oauth_error", logger.contains("oauth_error"))
        }

    @Test
    fun `handleIntent when data != null, oauth_error 500, device is secure and reauth is true`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (error: AuthenticationError) -> Unit)
                        .invoke(oauthError500)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent,
                true,
                activity = mockFragmentActivity
            )

            verifyNoInteractions(mockSignOutUseCase)
            verifyNoInteractions(mockSavePersistentId)
            verify(mockNavigator).navigate(LoginRoutes.SignInUnrecoverableError, true)
            assertThat("logger has no oauth_error", logger.contains("oauth_error"))
        }

    @Test
    fun `handleIntent when server_error, device is secure, reauth is true, attempt 1`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (error: AuthenticationError) -> Unit)
                        .invoke(serverError)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)
            whenever(mockCounter.getValue()).thenReturn(1)
            viewModel.handleActivityResult(
                mockIntent,
                true,
                activity = mockFragmentActivity
            )

            verifyNoInteractions(mockSignOutUseCase)
            verifyNoInteractions(mockSavePersistentId)
            verify(mockNavigator).navigate(LoginRoutes.SignInRecoverableError, true)
            assertThat("logger has no server_error", logger.contains("server_error"))
        }

    @Test
    fun `handleIntent when server_error, device is secure, reauth is true, attempt 3`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (error: AuthenticationError) -> Unit)
                        .invoke(serverError)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)
            whenever(mockCounter.getValue()).thenReturn(3)
            viewModel.handleActivityResult(
                mockIntent,
                true,
                activity = mockFragmentActivity
            )

            verifyNoInteractions(mockSignOutUseCase)
            verifyNoInteractions(mockSavePersistentId)
            verify(mockNavigator).navigate(LoginRoutes.SignInUnrecoverableError, true)
            assertThat("logger has no server_error", logger.contains("server_error"))
        }

    @Test
    fun `handleIntent when data != null && token_error, device is secure and reauth is true`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (error: AuthenticationError) -> Unit)
                        .invoke(tokenError400)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent,
                true,
                activity = mockFragmentActivity
            )

            verifyNoInteractions(mockSignOutUseCase)
            verifyNoInteractions(mockSavePersistentId)
            verify(mockNavigator).navigate(LoginRoutes.SignInUnrecoverableError, true)
            assertThat("logger has no token_error", logger.contains("token_error"))
        }

    @Test
    fun `handleIntent when data == null`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            whenever(mockIntent.data).thenReturn(null)

            viewModel.handleActivityResult(
                mockIntent,
                activity = mockFragmentActivity
            )

            verifyNoInteractions(mockSaveTokens)
            verifyNoInteractions(mockSaveTokenExpiry)
            verifyNoInteractions(mockTokenRepository)
            verifyNoInteractions(mockNavigator)
            verifyNoInteractions(mockSavePersistentId)
        }

    @Test
    fun `When login redirect fails - it displays sign in error screen`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (error: Throwable?) -> Unit).invoke(Throwable())
                }

            viewModel.handleActivityResult(
                mockIntent,
                activity = mockFragmentActivity
            )

            verifyNoInteractions(mockSaveTokens)
            verifyNoInteractions(mockSaveTokenExpiry)
            verifyNoInteractions(mockTokenRepository)
            verifyNoInteractions(mockSavePersistentId)
            verify(mockNavigator).navigate(LoginRoutes.SignInRecoverableError, true)
            assertThat("logger has log", logger.size == 1)
        }

    @Test
    fun `When login redirect fails, null throwable - it displays sign in error screen`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (error: Throwable?) -> Unit).invoke(null)
                }

            viewModel.handleActivityResult(mockIntent, activity = mockFragmentActivity)

            verifyNoInteractions(mockSaveTokens)
            verifyNoInteractions(mockSaveTokenExpiry)
            verifyNoInteractions(mockTokenRepository)
            verifyNoInteractions(mockSavePersistentId)
            verify(mockNavigator).navigate(LoginRoutes.SignInRecoverableError, true)
            assertThat("logger has no log", logger.size == 0)
        }

    @Test
    fun `When id token verification fails - displays sign in error screen`() =
        runTest {
            createMocks()
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(false)

            viewModel.handleActivityResult(
                mockIntent,
                activity = mockFragmentActivity
            )

            verifyNoInteractions(mockSaveTokens)
            verifyNoInteractions(mockSaveTokenExpiry)
            verifyNoInteractions(mockTokenRepository)
            verifyNoInteractions(mockSavePersistentId)
            verify(mockNavigator).navigate(LoginRoutes.SignInRecoverableError, true)
        }

    @Test
    fun `When login successful and wallet enabled - show BioOptIn`() = runTest {
        createMocks(true)
        val mockIntent: Intent = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
            .thenAnswer {
                (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }
        whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
            .thenReturn(true)

        viewModel.handleActivityResult(
            mockIntent,
            activity = mockFragmentActivity
        )

        verify(localAuthManager).enforceAndSet(eq(true), any(), any(), any())
    }

    @Test
    fun `When login successful and wallet disabled - show BioOptIn`() = runTest {
        createMocks(true, true)
        (mockFeatureFlags as InMemoryFeatureFlags).minus(WalletFeatureFlag.ENABLED)
        val mockIntent: Intent = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
            .thenAnswer {
                (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
            }
        whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
            .thenReturn(true)

        viewModel.handleActivityResult(
            mockIntent,
            activity = mockFragmentActivity
        )

        verify(localAuthManager).enforceAndSet(eq(false), any(), any(), any())
    }

    @Test
    fun `check nav to dev panel calls navigator correctly`() {
        createMocks()

        viewModel.navigateToDevPanel()

        verify(mockNavigator).openDeveloperPanel()
    }

    @Test
    fun `check nav to offline error calls navigator correctly`() {
        createMocks()

        viewModel.navigateToOfflineError()

        verify(mockNavigator).navigate(ErrorRoutes.Offline, false)
    }

    @Test
    fun `check abort login works as expected`() = runTest {
        createMocks()
        val mockIntent: Intent = mock()

        whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
            .thenAnswer {
                runBlocking {
                    delay(10000)
                    assert(viewModel.loading.value)
                    viewModel.abortLogin(any())
                }
            }

        assertFalse(viewModel.loading.value)
    }

    private fun createMocks(isLocalAuthMocked: Boolean = false, noFeatureFlags: Boolean = false) {
        mockFragmentActivity = mock()
        localAuthPreferenceRepo = mock()
        deviceBiometricsManager = mock()
        analyticsLogger = mock()
        localAuthManager = if (isLocalAuthMocked) {
            mock()
        } else {
            LocalAuthManagerImpl(
                localAuthPreferenceRepo,
                deviceBiometricsManager,
                analyticsLogger
            )
        }
        mockTokenRepository = mock()
        mockAutoInitialiseSecureStore = mock()
        mockVerifyIdToken = mock()
        mockNavigator = mock()
        mockSaveTokens = mock()
        mockSavePersistentId = mock()
        mockSaveTokenExpiry = mock()
        mockHandleRemoteLogin = mock()
        mockHandleLoginRedirect = mock()
        mockSignOutUseCase = mock()
        mockFeatureFlags = if (noFeatureFlags) {
            InMemoryFeatureFlags()
        } else {
            InMemoryFeatureFlags(
                WalletFeatureFlag.ENABLED
            )
        }
        mockOnlineChecker = mock()
        mockCounter = mock()

        viewModel =
            WelcomeScreenViewModel(
                mockContext,
                localAuthManager,
                mockTokenRepository,
                mockAutoInitialiseSecureStore,
                mockVerifyIdToken,
                mockNavigator,
                mockSaveTokens,
                mockSavePersistentId,
                mockSaveTokenExpiry,
                mockHandleRemoteLogin,
                mockHandleLoginRedirect,
                mockSignOutUseCase,
                logger,
                mockFeatureFlags,
                mockOnlineChecker,
                mockCounter
            )

        whenever(mockContext.getString(any(), any())).thenReturn("testUrl")
        whenever(mockContext.getString(any())).thenReturn("test")
    }
}
