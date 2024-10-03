package uk.gov.onelogin.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.e2e.controller.TestCase

@HiltAndroidTest
class SimpleTextPageTest : TestCase() {
    @get:Rule(order = 3)
    val composeTestRule = createComposeRule()

    @Test
    fun testSimpleTextPage() {
        val testText = "OneLogin"
        composeTestRule.setContent {
            SimpleTextPage(text = R.string.app_name)
        }

        composeTestRule.onNodeWithText(testText).assertIsDisplayed()
    }
}
