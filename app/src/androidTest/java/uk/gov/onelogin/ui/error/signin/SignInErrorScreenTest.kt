package uk.gov.onelogin.ui.error.signin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class SignInErrorScreenTest : TestCase() {
    private val title = hasText(resources.getString(R.string.app_signInErrorTitle))
    private val body = hasText(resources.getString(R.string.app_signInErrorBody))
    private val button = hasText(resources.getString(R.string.app_closeButton))

    private val onClick: () -> Unit = mock()

    @Before
    fun setupNavigation() {
        composeTestRule.setContent {
            SignInErrorScreen(onClick)
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
        verify(onClick).invoke()
    }
}
