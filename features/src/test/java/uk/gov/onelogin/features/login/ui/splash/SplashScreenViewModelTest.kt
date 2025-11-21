package uk.gov.onelogin.features.login.ui.splash

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.network.online.OnlineChecker
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.data.MainNavRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry
import uk.gov.onelogin.features.appinfo.domain.AppInfoService
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchange
import uk.gov.onelogin.features.login.domain.signin.locallogin.HandleLocalLogin
import uk.gov.onelogin.features.login.ui.signin.splash.SplashScreenViewModel
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class SplashScreenViewModelTest {
    private lateinit var mockOnlineChecker: OnlineChecker
    private lateinit var mockRefreshExchange: RefreshExchange
    private val mockHandleLocalLogin: HandleLocalLogin = mock()
    private val mockNavigator: Navigator = mock()
    private val mockLifeCycleOwner: LifecycleOwner = mock()
    private val mockActivity: FragmentActivity = mock()
    private val mockAppInfoService: AppInfoService = mock()
    private val mockSignOutUseCase: SignOutUseCase = mock()
    private val mockAutoInitialiseSecureStore: AutoInitialiseSecureStore = mock()
    private val mockGetRefreshTokenExp: GetTokenExpiry = mock()

    private lateinit var viewModel: SplashScreenViewModel

    @BeforeEach
    fun setup() {
        mockOnlineChecker = mock()
        mockRefreshExchange = mock()
        viewModel = SplashScreenViewModel(
            mockNavigator,
            mockHandleLocalLogin,
            mockAppInfoService,
            mockSignOutUseCase,
            mockAutoInitialiseSecureStore,
            mockOnlineChecker,
            mockRefreshExchange,
            mockGetRefreshTokenExp
        )
        whenever(mockOnlineChecker.isOnline()).thenReturn(true)
        whenever(mockGetRefreshTokenExp()).thenReturn(100)
    }

    @Test
    fun loginFailsWithSecureStoreError() =
        runTest {
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(
                        LocalAuthStatus.SecureStoreError
                    )
                }
            viewModel.login(mockActivity)

            verify(mockAutoInitialiseSecureStore).initialise(null)
            verify(mockNavigator).goBack()
            verify(mockNavigator).navigate(SignOutRoutes.Info, false)
        }

    @Test
    fun loginFailsWithBioCheckFailed() =
        runTest {
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(
                        LocalAuthStatus.BioCheckFailed
                    )
                }
            viewModel.login(mockActivity)

            verify(mockAutoInitialiseSecureStore).initialise(null)
            verifyNoInteractions(mockNavigator)
        }

    @Test
    fun loginSuccess() =
        runTest {
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(
                        LocalAuthStatus.Success(mapOf("key" to "token"))
                    )
                }
            viewModel.login(mockActivity)

            verify(mockAutoInitialiseSecureStore).initialise(null)
            verify(mockNavigator).goBack()
            verify(mockNavigator).navigate(MainNavRoutes.Start, false)
        }

    @Test
    fun loginRequiresRefresh() =
        runTest {
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(
                        LocalAuthStatus.ManualSignIn
                    )
                }
            viewModel.login(mockActivity)

            verify(mockAutoInitialiseSecureStore).initialise(null)
            verify(mockNavigator).goBack()
            verify(mockSignOutUseCase).invoke()
            verify(mockNavigator).navigate(LoginRoutes.AnalyticsOptIn)
        }

    @Test
    fun loginRequiresReAuth() =
        runTest {
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(
                        LocalAuthStatus.ReAuthSignIn
                    )
                }
            viewModel.login(mockActivity)

            verify(mockAutoInitialiseSecureStore).initialise(null)
            verify(mockNavigator).goBack()
            verify(mockNavigator).navigate(SignOutRoutes.Info, false)
        }

    @Test
    fun loginReturnsUserCancelled() =
        runTest {
            whenever(mockRefreshExchange.getTokens(any(), any()))
                .thenAnswer {
                    (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(
                        LocalAuthStatus.UserCancelled
                    )
                }
            viewModel.login(mockActivity)

            verify(mockAutoInitialiseSecureStore).initialise(null)
            verifyNoInteractions(mockNavigator)
            assertTrue(viewModel.showUnlock.value)
            assertFalse(viewModel.loading.value)
        }

    @Test
    fun allowsSubsequentLoginCallsFromLockScreen() =
        runTest {
            // WHEN on resume called more than once
            viewModel.onResume(mockLifeCycleOwner)
            viewModel.onResume(mockLifeCycleOwner)

            // AND we call login
            viewModel.login(mockActivity)

            // THEN the autoinitialise is being called
            verify(mockAutoInitialiseSecureStore).initialise(null)
            // THEN do NOT login (as the app will be going to background)
            verify(mockRefreshExchange, times(1))
                .getTokens(any(), any())
        }
}
