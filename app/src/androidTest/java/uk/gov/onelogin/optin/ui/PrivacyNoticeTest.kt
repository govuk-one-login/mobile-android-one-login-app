package uk.gov.onelogin.optin.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class PrivacyNoticeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun canBeClicked() {
        // Given a PrivacyNotice composable
        var actual = false
        composeTestRule.setContent {
            PrivacyNotice(modifier = Modifier, "Test Link") {
                actual = true
            }
        }
        val clickableText = hasTestTag(NOTICE_TAG)
        // When clicking on the text
        composeTestRule.onNode(clickableText).isDisplayed()
        composeTestRule.onNode(clickableText).performClick()
        // Then the onPrivacyNotice code block is called
        assertEquals(true, actual)
    }

    @Test
    fun hasIcon() {
        // Given a PrivacyNotice composable
        composeTestRule.setContent {
            PrivacyNotice(modifier = Modifier, "Test Link") {}
        }
        // Then an icon is visible
        val icon = hasTestTag(ICON_TAG)
        composeTestRule.onNode(icon).isDisplayed()
    }
}
