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
import org.junit.jupiter.api.BeforeEach
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
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.data.MainNavRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken
import uk.gov.onelogin.core.tokens.domain.save.SavePersistentId
import uk.gov.onelogin.core.tokens.domain.save.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
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
class WelcomeScreenViewModelTest {
    private val mockContext: Context = mock()
    private val mockFragmentActivity: FragmentActivity = mock()
    private val localAuthPreferenceRepo: LocalAuthPreferenceRepository = mock()
    private val deviceBiometricsManager: DeviceBiometricsManager = mock()
    private val analyticsLogger: AnalyticsLogger = mock()
    private val localAuthManager: LocalAuthManager = LocalAuthManagerImpl(
        localAuthPreferenceRepo,
        deviceBiometricsManager,
        analyticsLogger
    )
    private val mockTokenRepository: TokenRepository = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()
    private val mockVerifyIdToken: VerifyIdToken = mock()
    private val mockNavigator: Navigator = mock()
    private val mockFeatureFlags: FeatureFlags = InMemoryFeatureFlags(
        WalletFeatureFlag.ENABLED
    )
    private val mockOnlineChecker: OnlineChecker = mock()
    private val mockSaveTokens: SaveTokens = mock()
    private val mockSaveTokenExpiry: SaveTokenExpiry = mock()
    private val mockHandleRemoteLogin: HandleRemoteLogin = mock()
    private val mockHandleLoginRedirect: HandleLoginRedirect = mock()
    private val mockSignOutUseCase: SignOutUseCase = mock()
    private val mockSavePersistentId: SavePersistentId = mock()
    private val logger = SystemLogger()

    private val testAccessToken = "testAccessToken"
    private var testIdToken: String = "testIdToken"
    private val tokenResponse =
        TokenResponse(
            "testType",
            testAccessToken,
            1L,
            testIdToken,
            "testRefreshToken"
        )
    private val accessDeniedError = AuthenticationError(
        "access_denied",
        AuthenticationError.ErrorType.ACCESS_DENIED
    )
    private val oauthError = AuthenticationError(
        "oauth_error",
        AuthenticationError.ErrorType.OAUTH
    )

    private val viewModel =
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
            mockOnlineChecker
        )

    @BeforeEach
    fun setup() {
        whenever(mockContext.getString(any(), any())).thenReturn("testUrl")
        whenever(mockContext.getString(any())).thenReturn("test")
    }

    @Test
    fun `handleIntent when data != null, device secure, no biometrics, verify id token success`() =
        runTest {
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
            whenever(deviceBiometricsManager.getCredentialStatus())
                .thenReturn(DeviceBiometricsStatus.UNKNOWN)
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
            verify(mockSaveTokenExpiry).invoke(tokenResponse.accessTokenExpirationTime)
            verify(localAuthPreferenceRepo)
                .setLocalAuthPref(LocalAuthPreference.Enabled(false))
            verify(mockAutoInitialiseSecureStore, times(1)).initialise()
            verify(mockNavigator).navigate(MainNavRoutes.Start, true)
        }

    @Test
    fun `when data != null, device secure, verify id token success, bio pref set to biometrics`() =
        runTest {
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
            verify(mockSaveTokenExpiry).invoke(tokenResponse.accessTokenExpirationTime)
            verify(localAuthPreferenceRepo, times(0)).setLocalAuthPref(any())
            verify(mockAutoInitialiseSecureStore, times(1)).initialise()
            verify(mockNavigator).navigate(MainNavRoutes.Start, true)
        }

    @Test
    fun `handleIntent when data != null and device is secure with ok biometrics`() =
        runTest {
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

            verify(mockSaveTokenExpiry).invoke(tokenResponse.accessTokenExpirationTime)
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockSavePersistentId).invoke()
            verify(localAuthPreferenceRepo, times(0)).setLocalAuthPref(any())
        }

    @Test
    fun `when data != null and device is secure with ok biometrics and pref set to none`() =
        runTest {
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

            verify(mockSaveTokenExpiry).invoke(tokenResponse.accessTokenExpirationTime)
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockSavePersistentId).invoke()
            verify(localAuthPreferenceRepo, times(0)).setLocalAuthPref(any())
        }

    @Test
    fun `handleIntent when data != null and device is not secure`() =
        runTest {
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
            verify(mockSaveTokenExpiry).invoke(tokenResponse.accessTokenExpirationTime)
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockSavePersistentId).invoke()
            verify(localAuthPreferenceRepo).setLocalAuthPref(LocalAuthPreference.Disabled)
            verify(mockNavigator).navigate(MainNavRoutes.Start, true)
        }

    @Test
    fun `handleIntent when data != null, device not secure and reauth is true`() =
        runTest {
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

            verify(mockSaveTokenExpiry).invoke(tokenResponse.accessTokenExpirationTime)
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockSavePersistentId).invoke()
            verify(mockNavigator).goBack()
            verifyNoInteractions(mockSaveTokens)
        }

    @Test
    fun `handleIntent when data != null, device is secure and reauth is true`() =
        runTest {
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

            verify(mockSaveTokenExpiry).invoke(tokenResponse.accessTokenExpirationTime)
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockSavePersistentId).invoke()
            verify(mockNavigator).goBack()
            verify(mockSaveTokens).invoke()
        }

    @Test
    fun `handleIntent when data != null && access_denied, device is secure and reauth is true`() =
        runTest {
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
            verify(mockNavigator).navigate(LoginRoutes.SignInError, true)
            assertThat("logger has log", logger.contains("oauth_error"))
        }

    @Test
    fun `handleIntent when data == null`() =
        runTest {
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
            verify(mockNavigator).navigate(LoginRoutes.SignInError, true)
            assertThat("logger has log", logger.size == 1)
        }

    @Test
    fun `When login redirect fails, null throwable - it displays sign in error screen`() =
        runTest {
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
            verify(mockNavigator).navigate(LoginRoutes.SignInError, true)
            assertThat("logger has no log", logger.size == 0)
        }

    @Test
    fun `When id token verification fails - displays sign in error screen`() =
        runTest {
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
            verify(mockNavigator).navigate(LoginRoutes.SignInError, true)
        }

    @Test
    fun `check nav to dev panel calls navigator correctly`() {
        viewModel.navigateToDevPanel()

        verify(mockNavigator).openDeveloperPanel()
    }

    @Test
    fun `check nav to offline error calls navigator correctly`() {
        viewModel.navigateToOfflineError()

        verify(mockNavigator).navigate(ErrorRoutes.Offline, false)
    }

    @Test
    fun `check abort login works as expected`() = runTest {
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
}
