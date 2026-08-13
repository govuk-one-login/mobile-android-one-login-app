package uk.gov.onelogin.features.component.home.idcheck.ui

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.features.R
import uk.gov.android.ui.componentsv2.R as UiComponentsR
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.extensions.setupComposeTestRule
import uk.gov.onelogin.features.home.idcheck.ui.HowToProveYourIdentityModal

@RunWith(AndroidJUnit4::class)
class HowToProveYourIdentityModalScreenTest : FragmentActivityTestCase() {
    private val onGoToGovUkClick = mock<() -> Unit>()
    private val onDismissRequest = mock<() -> Unit>()

    @Test
    fun whenGoToGovUkClickedItInvokesCallback() {
        setupScreen()
        composeTestRule
            .onNodeWithText(
                context.getString(R.string.app_proveYourIdentityGuidanceLink),
                substring = true,
            ).performClick()

        verify(onGoToGovUkClick).invoke()
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

        verify(onDismissRequest).invoke()
    }

    private fun setupScreen() {
        composeTestRule.setupComposeTestRule { _ ->
            HowToProveYourIdentityModal(
                onDismissRequest = onDismissRequest,
                onGoToGovUkWebsiteClick = onGoToGovUkClick,
            )
        }
    }
}
