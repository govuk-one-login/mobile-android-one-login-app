package uk.gov.onelogin.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.AnnotatedString
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import uk.gov.onelogin.e2e.controller.TestCase

@HiltAndroidTest
class ClickableTextTest : TestCase() {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testClickableTextClick() {
        var clickIndex = -1
        composeTestRule.setContent {
            ClickableText(
                text = AnnotatedString("Click Me"),
                modifier = Modifier.testTag("ClickableText"),
                onClick = { index ->
                    clickIndex = index
                }
            )
        }

        // Perform a click in the middle of the text
        composeTestRule.onNodeWithTag("ClickableText", useUnmergedTree = true)
            .performTouchInput {
                click(center)
            }

        // Assert that the click was received in the correct range
        assert(clickIndex in 4..7) // "Click Me" - click should be somewhere within "Me"
    }

    @Test
    fun testClickableTextClickOutsideText() {
        var clicked = false
        composeTestRule.setContent {
            ClickableText(
                text = AnnotatedString("Click Me"),
                modifier = Modifier.testTag("ClickableText"),
                onClick = {
                    clicked = true
                }
            )
        }

        // Perform a click outside the bounds of the text
        composeTestRule.onNodeWithTag("ClickableText", useUnmergedTree = true)
            .performTouchInput {
                click(bottomLeft.copy(x = bottomLeft.x - 10f)) // Click outside to the left
            }

        // Assert that the click was not handled
        assert(!clicked)
    }
}
