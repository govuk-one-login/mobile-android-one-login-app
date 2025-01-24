package uk.gov.onelogin.ui.loading

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.Espresso
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.junit.Test
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class LoadingScreenTest : TestCase() {
    private var onBackPress = 0

    @Test
    fun verifyComponents() {
        composeTestRule.setContent {
            LoadingScreen { onBackPress++ }
        }

        composeTestRule.onNodeWithTag(LOADING_SCREEN_BOX).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LOADING_SCREEN_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LOADING_SCREEN_PROGRESS_INDICATOR).assertIsDisplayed()

        Espresso.pressBack()

        assertEquals(1, onBackPress)
    }

    @Test
    fun preview() {
        composeTestRule.setContent {
            LoadingPreview()
        }

        composeTestRule.onNodeWithTag(LOADING_SCREEN_BOX).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LOADING_SCREEN_TEXT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(LOADING_SCREEN_PROGRESS_INDICATOR).assertIsDisplayed()
    }
}
