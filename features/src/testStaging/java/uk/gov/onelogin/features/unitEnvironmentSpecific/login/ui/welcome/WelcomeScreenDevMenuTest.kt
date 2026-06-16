package uk.gov.onelogin.features.unitEnvironmentSpecific.login.ui.welcome

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v2.Logger
import uk.gov.logging.testdouble.v2.SystemLogger
import uk.gov.onelogin.core.localauth.domain.LocalAuthPrefResetUseCase
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreenAnalyticsViewModel
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.login.LoginViewModel
import uk.gov.onelogin.features.login.domain.signin.remotelogin.RemoteLogin
import uk.gov.onelogin.features.login.ui.signin.welcome.SignInAnalyticsViewModel
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreen
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreenViewModel
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@RunWith(AndroidJUnit4::class)
class WelcomeScreenDevMenuTest : FragmentActivityTestCase() {
    private lateinit var navigator: Navigator
    private lateinit var remoteLogin: RemoteLogin
    private lateinit var onlineChecker: OnlineChecker
    private lateinit var getPersistentId: GetPersistentId
    private val logger: Logger = SystemLogger()
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var localAuthPrefResetUseCase: LocalAuthPrefResetUseCase
    private lateinit var viewModel: WelcomeScreenViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: SignInAnalyticsViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loadingAnalyticsViewModel: LoadingScreenAnalyticsViewModel

    private val devButton = hasText(resources.getString(R.string.app_developer_button))

    @Before
    fun setup() {
        navigator = mock()
        remoteLogin = mock()
        onlineChecker = mock()
        analytics = mock()
        signOutUseCase = mock()
        localAuthPrefResetUseCase = mock()
        getPersistentId = mock()
        viewModel =
            WelcomeScreenViewModel(
                navigator,
            )
        loginViewModel =
            LoginViewModel(
                navigator,
                onlineChecker,
                remoteLogin,
                getPersistentId,
                signOutUseCase,
                localAuthPrefResetUseCase,
                logger
            )
        analyticsViewModel = SignInAnalyticsViewModel(context, analytics)
        loadingAnalyticsViewModel = LoadingScreenAnalyticsViewModel(context, analytics)
        composeTestRule.setContent {
            WelcomeScreen(viewModel, loginViewModel, analyticsViewModel, loadingAnalyticsViewModel)
        }
    }

    @Test
    fun verifyDevMenuClick() {
        composeTestRule.onAllNodes(devButton)[0].performClick()

        verify(navigator).openDeveloperPanel()
    }
}
