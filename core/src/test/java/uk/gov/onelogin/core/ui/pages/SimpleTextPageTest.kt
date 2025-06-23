package uk.gov.onelogin.core.ui.pages

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.core.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class SimpleTextPageTest : FragmentActivityTestCase() {
    @Test
    fun testSimpleTextPage() {
        val testText = "One Login"
        composeTestRule.setContent {
            SimpleTextPage(text = R.string.one_login_app_name)
        }

        composeTestRule.onNodeWithText(testText).assertIsDisplayed()
    }
}
