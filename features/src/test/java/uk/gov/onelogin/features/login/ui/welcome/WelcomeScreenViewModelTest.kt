package uk.gov.onelogin.features.login.ui.welcome

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlin.test.assertFalse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
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
import uk.gov.android.network.online.OnlineChecker
import uk.gov.onelogin.core.biometrics.data.BiometricPreference
import uk.gov.onelogin.core.biometrics.data.BiometricStatus
import uk.gov.onelogin.core.biometrics.domain.BiometricPreferenceHandler
import uk.gov.onelogin.core.biometrics.domain.CredentialChecker
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.data.MainNavRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken
import uk.gov.onelogin.core.tokens.domain.save.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.login.domain.signin.loginredirect.HandleLoginRedirect
import uk.gov.onelogin.features.login.domain.signin.remotelogin.HandleRemoteLogin
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreenViewModel
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class WelcomeScreenViewModelTest {
    private val mockContext: Context = mock()
    private val mockCredChecker: CredentialChecker = mock()
    private val mockBioPrefHandler: BiometricPreferenceHandler = mock()
    private val mockTokenRepository: TokenRepository = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()
    private val mockVerifyIdToken: VerifyIdToken = mock()
    private val mockNavigator: Navigator = mock()
    private val mockOnlineChecker: OnlineChecker = mock()
    private val mockSaveTokens: SaveTokens = mock()
    private val mockSaveTokenExpiry: SaveTokenExpiry = mock()
    private val mockHandleRemoteLogin: HandleRemoteLogin = mock()
    private val mockHandleLoginRedirect: HandleLoginRedirect = mock()
    private val mockSignOutUseCase: SignOutUseCase = mock()

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
            mockCredChecker,
            mockBioPrefHandler,
            mockTokenRepository,
            mockAutoInitialiseSecureStore,
            mockVerifyIdToken,
            mockNavigator,
            mockSaveTokens,
            mockSaveTokenExpiry,
            mockHandleRemoteLogin,
            mockHandleLoginRedirect,
            mockSignOutUseCase,
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
            whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
            whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.UNKNOWN)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent
            )

            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockSaveTokenExpiry).invoke(tokenResponse.accessTokenExpirationTime)
            verify(mockBioPrefHandler).setBioPref(BiometricPreference.PASSCODE)
            verify(mockAutoInitialiseSecureStore, times(1)).initialise()
            verify(mockNavigator).navigate(MainNavRoutes.Start, true)
        }

    @Test
    fun `when data != null, device secure, verify id token success, bio pref set to biometrics`() =
        runTest {
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
            whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.SUCCESS)
            whenever(mockBioPrefHandler.getBioPref()).thenReturn(BiometricPreference.BIOMETRICS)
            // Login redirect fires `onSuccess`
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            // re-authenticate is false by default
            viewModel.handleActivityResult(
                mockIntent
            )

            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockSaveTokenExpiry).invoke(tokenResponse.accessTokenExpirationTime)
            verify(mockBioPrefHandler, times(0)).setBioPref(any())
            verify(mockAutoInitialiseSecureStore, times(1)).initialise()
            verify(mockNavigator).navigate(MainNavRoutes.Start, true)
        }

    @Test
    fun `handleIntent when data != null and device is secure with ok biometrics`() =
        runTest {
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
            whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.SUCCESS)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent
            )

            verify(mockSaveTokenExpiry).invoke(tokenResponse.accessTokenExpirationTime)
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockBioPrefHandler, times(0)).setBioPref(any())
            verify(mockNavigator).navigate(LoginRoutes.BioOptIn, true)
        }

    @Test
    fun `when data != null and device is secure with ok biometrics and pref set to none`() =
        runTest {
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
            whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.SUCCESS)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)
            whenever(mockBioPrefHandler.getBioPref()).thenReturn(BiometricPreference.NONE)

            viewModel.handleActivityResult(
                mockIntent
            )

            verify(mockSaveTokenExpiry).invoke(tokenResponse.accessTokenExpirationTime)
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockBioPrefHandler, times(0)).setBioPref(any())
            verify(mockNavigator).navigate(LoginRoutes.BioOptIn, true)
        }

    @Test
    fun `handleIntent when data != null and device is not secure`() =
        runTest {
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(mockCredChecker.isDeviceSecure()).thenReturn(false)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent
            )

            verifyNoInteractions(mockSaveTokens)
            verify(mockSaveTokenExpiry).invoke(tokenResponse.accessTokenExpirationTime)
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockBioPrefHandler).setBioPref(BiometricPreference.NONE)
            verify(mockNavigator).navigate(MainNavRoutes.Start, true)
        }

    @Test
    fun `handleIntent when data != null, device not secure and reauth is true`() =
        runTest {
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(mockCredChecker.isDeviceSecure()).thenReturn(false)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent,
                true
            )

            verify(mockSaveTokenExpiry).invoke(tokenResponse.accessTokenExpirationTime)
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockNavigator).goBack()
            verifyNoInteractions(mockSaveTokens)
            verifyNoInteractions(mockBioPrefHandler)
        }

    @Test
    fun `handleIntent when data != null, device is secure and reauth is true`() =
        runTest {
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[2] as (token: TokenResponse) -> Unit).invoke(tokenResponse)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent,
                true
            )

            verify(mockSaveTokenExpiry).invoke(tokenResponse.accessTokenExpirationTime)
            verify(mockTokenRepository).setTokenResponse(tokenResponse)
            verify(mockNavigator).goBack()
            verify(mockSaveTokens).invoke()
            verifyNoInteractions(mockBioPrefHandler)
        }

    @Test
    fun `handleIntent when data != null && access_denied, device is secure and reauth is true`() =
        runTest {
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (error: AuthenticationError) -> Unit)
                        .invoke(accessDeniedError)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent,
                true
            )

            verify(mockSignOutUseCase).invoke()
            verify(mockNavigator).navigate(SignOutRoutes.ReAuthError)
        }

    @Test
    fun `handleIntent when data != null && oauth_error, device is secure and reauth is true`() =
        runTest {
            val mockIntent: Intent = mock()
            val mockUri: Uri = mock()

            whenever(mockIntent.data).thenReturn(mockUri)
            whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
            whenever(mockHandleLoginRedirect.handle(eq(mockIntent), any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (error: AuthenticationError) -> Unit)
                        .invoke(oauthError)
                }
            whenever(mockVerifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
                .thenReturn(true)

            viewModel.handleActivityResult(
                mockIntent,
                true
            )

            verifyNoInteractions(mockSignOutUseCase)
            verify(mockNavigator).navigate(LoginRoutes.SignInError, true)
        }

    @Test
    fun `handleIntent when data == null`() =
        runTest {
            val mockIntent: Intent = mock()
            whenever(mockIntent.data).thenReturn(null)

            viewModel.handleActivityResult(
                mockIntent
            )

            verifyNoInteractions(mockSaveTokens)
            verifyNoInteractions(mockSaveTokenExpiry)
            verifyNoInteractions(mockTokenRepository)
            verifyNoInteractions(mockBioPrefHandler)
            verifyNoInteractions(mockNavigator)
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
                mockIntent
            )

            verifyNoInteractions(mockSaveTokens)
            verifyNoInteractions(mockSaveTokenExpiry)
            verifyNoInteractions(mockTokenRepository)
            verifyNoInteractions(mockBioPrefHandler)
            verify(mockNavigator).navigate(LoginRoutes.SignInError, true)
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
                mockIntent
            )

            verifyNoInteractions(mockSaveTokens)
            verifyNoInteractions(mockSaveTokenExpiry)
            verifyNoInteractions(mockTokenRepository)
            verifyNoInteractions(mockBioPrefHandler)
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
