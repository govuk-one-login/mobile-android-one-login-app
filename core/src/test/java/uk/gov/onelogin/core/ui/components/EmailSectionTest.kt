package uk.gov.onelogin.core.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.core.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class EmailSectionTest : FragmentActivityTestCase() {
    private val email = "mock@email.com"

    @Test
    fun verifyDisplay() {
        composeTestRule.setContent {
            EmailSection(email)
        }

        composeTestRule.apply {
            onNodeWithTag(DIVIDER_TEST_TAG).assertIsDisplayed()

            onNodeWithTag(IMAGE_TEST_TAG).assertIsDisplayed()

            onNodeWithText(
                context.getString(R.string.app_settingsSignInDetailsTile),
                substring = true
            ).assertIsDisplayed()

            onNodeWithText(email, substring = true).assertIsDisplayed()
        }
    }
}
