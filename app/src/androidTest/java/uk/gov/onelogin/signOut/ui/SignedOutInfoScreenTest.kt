package uk.gov.onelogin.signOut.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class SignedOutInfoScreenTest : TestCase() {
    private var onPrimary = 0

    @Before
    fun setup() {
        composeTestRule.setContent {
            SignedOutInfoScreen(
                signIn = { onPrimary++ }
            )
        }
    }

    @Test
    fun verifyScreenDisplayed() {
        composeTestRule.apply {
            onNodeWithText(
                resources.getString(R.string.app_youveBeenSignedOutTitle)
            ).assertIsDisplayed()

            onNodeWithText(
                resources.getString(R.string.app_youveBeenSignedOutBody1)
            ).assertIsDisplayed()

            onNodeWithText(
                resources.getString(R.string.app_youveBeenSignedOutBody2)
            ).assertIsDisplayed()
        }
    }

    @Test
    fun verifyOnSignInClick() {
        composeTestRule.apply {
            onNodeWithText(
                resources.getString(R.string.app_SignInWithGovUKOneLoginButton)
            ).apply {
                assertIsDisplayed()
                performClick()
            }
        }

        assertEquals(1, onPrimary)
    }
}
