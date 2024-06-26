package uk.gov.onelogin.login.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class SignInErrorScreenTest : TestCase() {
    private val title = hasText(resources.getString(R.string.app_signInErrorTitle))
    private val body = hasText(resources.getString(R.string.app_signInErrorBody))
    private val button = hasText(resources.getString(R.string.app_closeButton))

    private var buttonClick = 0

    @Before
    fun setupNavigation() {
        composeTestRule.setContent {
            SignInErrorScreen(
                onClick = { buttonClick++ }
            )
        }
    }

    @Test
    fun verifyComponents() {
        composeTestRule.onNode(title).assertIsDisplayed()
        composeTestRule.onNode(body).assertIsDisplayed()
        composeTestRule.onNode(button).assertIsDisplayed()
    }

    @Test
    fun checkButtonClick() {
        composeTestRule.onNode(button).performClick()

        assertEquals(1, buttonClick)
    }
}
