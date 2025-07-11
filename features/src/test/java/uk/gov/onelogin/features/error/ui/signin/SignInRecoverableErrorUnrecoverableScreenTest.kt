package uk.gov.onelogin.features.error.ui.signin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
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
import uk.gov.onelogin.features.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class SignInRecoverableErrorUnrecoverableScreenTest : FragmentActivityTestCase() {
    private lateinit var analytics: AnalyticsLogger
    private lateinit var viewModel: SignInErrorAnalyticsViewModel

    private val title = hasText(resources.getString(R.string.app_signInErrorTitle))
    private val body = hasText(resources.getString(R.string.app_genericErrorPageBody))
    private val button = hasText(resources.getString(R.string.app_tryAgainButton))

    @Before
    fun setupNavigation() {
        analytics = mock()
        viewModel = SignInErrorAnalyticsViewModel(context, analytics)
    }

    @Test
    fun verifyComponents() {
        composeTestRule.setContent {
            SignInErrorUnrecoverableScreen(analyticsViewModel = viewModel)
        }
        composeTestRule.onNode(title).assertIsDisplayed()
        composeTestRule.onNode(body).assertIsDisplayed()
        composeTestRule.onNode(button).assertIsNotDisplayed()
        verify(analytics).logEventV3Dot1(
            SignInErrorAnalyticsViewModel.makeUnrecoverableScreenEvent(context)
        )
    }

    @Test
    fun checkBackClicked() {
        composeTestRule.setContent {
            SignInErrorUnrecoverableScreen(analyticsViewModel = viewModel)
        }
        Espresso.pressBack()
        verify(analytics).logEventV3Dot1(SignInErrorAnalyticsViewModel.makeBackEvent(context))
    }

    @Test
    fun preview() {
        composeTestRule.setContent {
            SignInErrorUnrecoverableScreenPreview()
        }
    }
}
