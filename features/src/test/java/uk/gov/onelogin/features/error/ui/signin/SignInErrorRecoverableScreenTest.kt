package uk.gov.onelogin.features.error.ui.signin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class SignInErrorRecoverableScreenTest : FragmentActivityTestCase() {
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: SignInErrorAnalyticsViewModel
    private lateinit var viewModel: SignInErrorRecoverableViewModel
    private lateinit var navigator: Navigator

    private val title = hasText(resources.getString(R.string.app_signInErrorTitle))
    private val body = hasText(resources.getString(R.string.app_signInErrorRecoverableBody))
    private val button = hasText(resources.getString(R.string.app_tryAgainButton))

    @Before
    fun setupNavigation() {
        analytics = mock()
        analyticsViewModel = SignInErrorAnalyticsViewModel(context, analytics)
        navigator = mock()
        viewModel = SignInErrorRecoverableViewModel(navigator)
    }

    @Test
    fun verifyComponents() {
        composeTestRule.setContent {
            SignInErrorRecoverableScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel
            )
        }
        composeTestRule.onNode(title).assertIsDisplayed()
        composeTestRule.onNode(body).assertIsDisplayed()
        composeTestRule.onNode(button).apply {
            assertIsDisplayed()
            performClick()
        }
        verify(analytics).logEventV3Dot1(
            SignInErrorAnalyticsViewModel.makeRecoverableScreenEvent(context)
        )
        verify(analytics).logEventV3Dot1(SignInErrorAnalyticsViewModel.makeButtonEvent(context))
        verify(navigator).navigate(LoginRoutes.Start, true)
    }

    @Test
    fun checkBackClicked() {
        composeTestRule.setContent {
            SignInErrorRecoverableScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel
            )
        }
        Espresso.pressBack()
        verify(analytics).logEventV3Dot1(SignInErrorAnalyticsViewModel.makeBackEvent(context))
        verify(navigator).navigate(LoginRoutes.Welcome, true)
    }

    @Test
    fun preview() {
        composeTestRule.setContent {
            SignInErrorRecoverableScreenPreview()
        }
    }
}
