package uk.gov.onelogin.core.ui.pages

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.core.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class TitledPageTest : FragmentActivityTestCase() {
    @Test
    fun titlePageDisplayed() {
        composeTestRule.setContent {
            TitledPage(R.string.app_homeTitle) {
                Text("test")
            }
        }

        composeTestRule.apply {
            onNodeWithText(
                context.getString(R.string.app_homeTitle),
                useUnmergedTree = true
            ).assertIsDisplayed()

            onNodeWithText("test").assertIsDisplayed()
        }
    }
}
