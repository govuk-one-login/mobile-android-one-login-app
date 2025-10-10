package uk.gov.onelogin.features.login.ui.splash

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.appinfo.domain.AppInfoService
import uk.gov.onelogin.features.login.domain.signin.locallogin.HandleLocalLogin
import uk.gov.onelogin.features.login.ui.signin.splash.SplashBody
import uk.gov.onelogin.features.login.ui.signin.splash.SplashScreen
import uk.gov.onelogin.features.login.ui.signin.splash.SplashScreenAnalyticsViewModel
import uk.gov.onelogin.features.login.ui.signin.splash.SplashScreenViewModel
import uk.gov.onelogin.features.optin.data.OptInRepository
import uk.gov.onelogin.features.optin.ui.OptInRequirementViewModel

@RunWith(AndroidJUnit4::class)
class SplashScreenDevMenuTest : FragmentActivityTestCase() {
    private lateinit var handleLocalLogin: HandleLocalLogin
    private lateinit var navigator: Navigator
    private lateinit var appInfoService: AppInfoService
    private lateinit var autoInitialiseSecureStore: AutoInitialiseSecureStore
    private lateinit var viewModel: SplashScreenViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: SplashScreenAnalyticsViewModel
    private var repository: OptInRepository = mock()
    private lateinit var optInViewModel: OptInRequirementViewModel

    private val splashIcon = hasTestTag(resources.getString(R.string.splashLogoTestTag))

    @Before
    fun setup() {
        whenever(repository.isOptInPreferenceRequired()).thenReturn(MutableStateFlow(false))
        handleLocalLogin = mock()
        navigator = mock()
        appInfoService = mock()
        autoInitialiseSecureStore = mock()
        viewModel = SplashScreenViewModel(
            navigator,
            handleLocalLogin,
            appInfoService,
            autoInitialiseSecureStore
        )
        analytics = mock()
        analyticsViewModel = SplashScreenAnalyticsViewModel(context, analytics)
        optInViewModel = OptInRequirementViewModel(repository)
    }

    @Test
    fun testDevMenuButton() {
        composeTestRule.setContent {
            SplashScreen(viewModel, analyticsViewModel, optInViewModel)
        }

        composeTestRule.onNode(splashIcon).assert(hasClickAction())
        composeTestRule.onNode(splashIcon).performClick()

        verify(navigator).openDeveloperPanel()
    }

    @Test
    fun onOpenDeveloperPortal() {
        // Given the SplashBody Composable
        var actual = false
        composeTestRule.setContent {
            SplashBody(
                isUnlock = false,
                loading = false,
                trackUnlockButton = { },
                onLogin = {},
                onOpenDeveloperPortal = { actual = true }
            )
        }
        // When clicking the `splashIcon`
        composeTestRule.onNode(splashIcon).performClick()
        // Then onOpenDeveloperPortal() is called and the variable is changed to true
        assertTrue(actual)
    }
}
