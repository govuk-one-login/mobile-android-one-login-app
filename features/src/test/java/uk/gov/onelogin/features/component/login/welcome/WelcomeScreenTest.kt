package uk.gov.onelogin.features.component.login.welcome

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreenAnalyticsViewModel
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.login.domain.signin.remotelogin.RemoteLogin
import uk.gov.onelogin.features.login.ui.signin.welcome.SignInAnalyticsViewModel
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomePreview
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreen
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreenViewModel

@RunWith(AndroidJUnit4::class)
class WelcomeScreenTest : FragmentActivityTestCase() {
    private lateinit var navigator: Navigator
    private lateinit var remoteLogin: RemoteLogin
    private lateinit var onlineChecker: OnlineChecker
    private lateinit var viewModel: WelcomeScreenViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: SignInAnalyticsViewModel
    private lateinit var loadingAnalyticsViewModel: LoadingScreenAnalyticsViewModel

    private val signInTitle = hasText(resources.getString(R.string.app_signInTitle))
    private val signInSubTitle1 = hasText(resources.getString(R.string.app_signInBody1))

//    private val signInSubTitle2 = hasText(resources.getString(R.string.app_signInBody2))
    private val signInButton = hasText(resources.getString(R.string.app_signInButton))

    @Before
    fun setup() {
        navigator = mock()
        remoteLogin = mock()
        onlineChecker = mock()
        analytics = mock()
        viewModel =
            WelcomeScreenViewModel(
                navigator,
                onlineChecker,
                remoteLogin
            )
        analyticsViewModel = SignInAnalyticsViewModel(context, analytics)
        loadingAnalyticsViewModel = LoadingScreenAnalyticsViewModel(context, analytics)
    }

    @Suppress("ForbiddenComment")
    @Test
    fun verifyComponents() {
        composeTestRule.setContent {
            WelcomeScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel,
                loadingAnalyticsViewModel = loadingAnalyticsViewModel
            )
        }

        composeTestRule.onNode(signInTitle).assertIsDisplayed()
        composeTestRule.onNode(signInSubTitle1).assertIsDisplayed()
        // TODO Fix breaking line below in buildRelease and StagingRelease flavours
        // composeTestRule.onNode(signInSubTitle2).assertIsDisplayed()
        composeTestRule.onAllNodes(signInButton)[0].assertIsDisplayed()
        // TODO: Add testTag to the icon in mobile ui to be able to test the icon on CentreAlignedScreen when contentDescription is empty
    }

    @Test
    fun opensWebLoginViaCustomTab() =
        runBlocking {
            whenever(onlineChecker.isOnline()).thenReturn(true)
            composeTestRule.setContent {
                WelcomeScreen(
                    analyticsViewModel = analyticsViewModel,
                    viewModel = viewModel,
                    loadingAnalyticsViewModel = loadingAnalyticsViewModel
                )
            }

            whenWeClickSignIn()

            verify(remoteLogin).start(any())
        }

    @Test
    fun opensNetworkErrorScreen() {
        givenWeAreOffline()

        whenWeClickSignIn()

        itOpensErrorScreen()
    }

    @Test
    fun screenViewAnalyticsLogOnResume() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val event = SignInAnalyticsViewModel.Companion.makeWelcomeViewEvent(context)
        composeTestRule.setContent {
            WelcomeScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel,
                loadingAnalyticsViewModel = loadingAnalyticsViewModel
            )
        }

        verify(analytics).logEventV3Dot1(event)
    }

    @Test
    fun signInAnalyticsLogOnSignInButton() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val event = SignInAnalyticsViewModel.Companion.makeSignInEvent(context)
        whenever(onlineChecker.isOnline()).thenReturn(true)
        composeTestRule.setContent {
            WelcomeScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel,
                loadingAnalyticsViewModel = loadingAnalyticsViewModel
            )
        }
        whenWeClickSignIn()
        verify(analytics).logEventV3Dot1(event)
    }

    @Test
    fun testBackButton() {
        composeTestRule.setContent {
            whenever(onlineChecker.isOnline()).thenReturn(true)
            WelcomeScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel,
                loadingAnalyticsViewModel = loadingAnalyticsViewModel
            )
        }

        composeTestRule.apply {
            activityRule.scenario.onActivity { activity ->
                activity.onBackPressedDispatcher.onBackPressed()
                assert(activity.isFinishing)
            }
        }
    }

    private fun whenWeClickSignIn() {
        composeTestRule.onAllNodes(signInButton)[0].performClick()
    }

    private fun givenWeAreOffline() {
        whenever(onlineChecker.isOnline()).thenReturn(false)
        composeTestRule.setContent {
            WelcomeScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel,
                loadingAnalyticsViewModel = loadingAnalyticsViewModel
            )
        }
    }

    private fun itOpensErrorScreen() {
        verify(navigator).navigate(ErrorRoutes.Offline)
    }

    @Test
    fun previewTest() {
        composeTestRule.setContent {
            WelcomePreview()
        }
    }
}
