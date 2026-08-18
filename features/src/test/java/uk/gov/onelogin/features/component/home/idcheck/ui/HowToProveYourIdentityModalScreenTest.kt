package uk.gov.onelogin.features.component.home.idcheck.ui

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.features.R
import uk.gov.logging.testdouble.analytics.FakeAnalyticsLogger
import uk.gov.android.ui.componentsv2.R as UiComponentsR
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.navigation.domain.WebNavigator
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.extensions.setupComposeTestRule
import uk.gov.onelogin.features.home.idcheck.ui.HowToProveYourIdentityModal
import uk.gov.onelogin.features.home.idcheck.ui.HowToProveYourIdentityModalAnalyticsViewModel
import uk.gov.onelogin.features.home.idcheck.ui.HowToProveYourIdentityModalViewModel

@RunWith(AndroidJUnit4::class)
class HowToProveYourIdentityModalScreenTest : FragmentActivityTestCase() {

    private val navigator: Navigator = mock()
    private val webNavigator: WebNavigator = mock()
    private val analyticsLogger = FakeAnalyticsLogger()

    private val viewModel = HowToProveYourIdentityModalViewModel(
        navigator = navigator,
        webNavigator = webNavigator,
        govUkSignInUrl = GOV_UK_SIGN_IN_URL,
    )

    private val analyticsViewModel = HowToProveYourIdentityModalAnalyticsViewModel(
        context = context,
        analyticsLogger = analyticsLogger,
    )

    @Test
    fun whenGoToGovUkClickedItInvokesCallback() {
        setupScreen()
        composeTestRule
            .onNodeWithText(
                context.getString(R.string.app_proveYourIdentityGuidanceLink),
                substring = true,
            ).performClick()

        verify(webNavigator).openWebBrowser(GOV_UK_SIGN_IN_URL)
    }

    @Test
    fun goToGovUkLinkContentIncludesIconContentDescription() {
        setupScreen()
        composeTestRule
            .onNodeWithText(
                context.getString(R.string.app_proveYourIdentityGuidanceLink),
                substring = true,
            ).assertTextEquals(
                // This is what is visible to screen readers
                "Go to the GOV.UK website Opens in web browser"
            )
    }

    @Test
    fun whenCloseClickedItInvokesOnDismissRequest() {
        setupScreen()
        composeTestRule
            .onNodeWithContentDescription(
                context.getString(UiComponentsR.string.close_icon_button),
                substring = true,
            ).performClick()

        verify(navigator).goBack()
    }

    @Test
    fun trackScreenViewAnalyticsOnDisplay() {
        setupScreen()

        val screenViewEvents = analyticsLogger.filter { it.isScreenView() }

        assertEquals(1, screenViewEvents.size)
    }

    private fun setupScreen() {
        composeTestRule.setupComposeTestRule { _ ->
            HowToProveYourIdentityModal(viewModel, analyticsViewModel)
        }
    }

    companion object {
        private const val GOV_UK_SIGN_IN_URL = "https://www.gov.uk/sign-in"
    }
}
