package uk.gov.onelogin.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class LoadingScreenTest : TestCase() {
    @Before
    fun setupNavigation() {
        composeTestRule.setContent {
            LoadingScreen()
        }
    }

    @Test
    fun verifyComponents() {
        composeTestRule.onNodeWithTag(LOADING_SCREEN_BOX).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LOADING_SCREEN_PROGRESS_INDICATOR).assertIsDisplayed()
    }
}
