package uk.gov.onelogin.ui.error

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class SignOutErrorScreenTest : TestCase() {

    private val errorTitle = hasText(resources.getString(R.string.app_signOutErrorTitle))
    private val errorBody = hasText(resources.getString(R.string.app_signOutErrorBody))
    private val primaryButton = hasText(resources.getString(R.string.app_exitButton))

    @Before
    fun setUp() {
        composeTestRule.setContent {
            SignOutErrorScreen()
        }
    }

    @Test
    fun signOutErrorScreen() {
        composeTestRule.onNode(errorTitle).assertIsDisplayed()
        composeTestRule.onNode(errorBody).assertIsDisplayed()
        composeTestRule.onNode(primaryButton).assertIsDisplayed()
    }
}
