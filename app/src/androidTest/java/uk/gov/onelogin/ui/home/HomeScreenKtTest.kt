package uk.gov.onelogin.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.ext.setupComposeTestRule

@HiltAndroidTest
class HomeScreenKtTest : TestCase() {
    @Test
    fun homeScreenDisplayed() {
        composeTestRule.setupComposeTestRule { _ ->
            HomeScreen()
        }

        composeTestRule.apply {
            onNodeWithText(
                resources.getString(R.string.app_homeTitle)
            ).assertIsDisplayed()

            onNodeWithTag(
                resources.getString(R.string.appCriCardTestTag),
                useUnmergedTree = true
            ).assertIsDisplayed()

            onNodeWithText("Developer Panel").assertIsDisplayed()
        }
    }
}
