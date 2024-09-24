package uk.gov.onelogin.appcheck.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class UpdateRequiredScreenTest : TestCase() {
    private var onPrimary = 0

    @Before
    fun setup() {
        composeTestRule.setContent {
            UpdateRequiredScreen(
                updateApp = { onPrimary++ }
            )
        }
    }

    @Test
    fun verifyScreenDisplayed() {
        composeTestRule.apply {
            onNodeWithContentDescription(
                resources.getString(R.string.app_updateApp_ContentDescription)
            ).assertIsDisplayed()

            onNodeWithText(
                resources.getString(R.string.app_updateApp_Title)
            ).assertIsDisplayed()

            onNodeWithText(
                resources.getString(R.string.app_updateAppBody1)
            ).assertIsDisplayed()

            onNodeWithText(
                resources.getString(R.string.app_updateAppBody2)
            ).assertIsDisplayed()
        }
    }

    @Test
    fun verifyOnSignInClick() {
        composeTestRule.apply {
            onNodeWithText(
                resources.getString(R.string.app_updateAppButton)
            ).apply {
                assertIsDisplayed()
                performClick()
            }
        }

        assertEquals(1, onPrimary)
    }
}
