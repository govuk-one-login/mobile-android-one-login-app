package uk.gov.onelogin.core.ui.pages

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
}
