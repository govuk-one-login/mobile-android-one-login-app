package uk.gov.onelogin.core.ui.pages

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.core.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class TitledLogoPageTest : FragmentActivityTestCase() {
    @Test
    fun titlePageDisplayed() {
        composeTestRule.setContent {
            TitledLogoPage(R.drawable.ic_onelogin_title) {
                Text("test")
            }
        }

        composeTestRule.apply {
            onNodeWithContentDescription(
                resources.getString(R.string.one_login_image_content_desc),
                useUnmergedTree = true
            ).assertIsDisplayed()

            onNodeWithText("test").assertIsDisplayed()
        }
    }
}
