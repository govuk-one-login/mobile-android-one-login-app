package uk.gov.onelogin.core.ui.pages

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.onelogin.core.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class EdgeToEdgePageTest : FragmentActivityTestCase() {
    @Test
    fun testEdgeToEdgePage() {
        val expectedText = "Test"
        composeTestRule.setContent {
            EdgeToEdgePage {
                Text(expectedText)
            }
        }

        composeTestRule.onNodeWithText(expectedText).assertExists()
    }

    @Test
    fun testPageWithModifier() {
        val expectedText = "Test"
        composeTestRule.setContent {
            EdgeToEdgePage(modifier = Modifier) {
                Text(expectedText)
            }
        }

        composeTestRule.onNodeWithText(expectedText).assertExists()
    }

    @Test
    fun testPageWithModifierWindowInsets() {
        val expectedText = "Test"
        composeTestRule.setContent {
            EdgeToEdgePage(modifier = Modifier.windowInsetsPadding(WindowInsets.displayCutout)) {
                Text(expectedText)
            }
        }

        composeTestRule.onNodeWithText(expectedText).assertExists()
    }

    fun testPageWithModifierNavigationBars() {
        val expectedText = "Test"
        composeTestRule.setContent {
            EdgeToEdgePage(modifier = Modifier.navigationBarsPadding()) {
                Text(expectedText)
            }
        }

        composeTestRule.onNodeWithText(expectedText).assertExists()
    }
}
