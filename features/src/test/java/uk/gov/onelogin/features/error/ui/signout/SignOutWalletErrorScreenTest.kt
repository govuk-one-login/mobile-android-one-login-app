package uk.gov.onelogin.features.error.ui.signout

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.times
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class SignOutWalletErrorScreenTest : FragmentActivityTestCase() {
    private val errorTitle = hasText(resources.getString(R.string.app_signOutErrorTitle))
    private val errorBody1 = hasText(resources.getString(R.string.app_signOutErrorBody1))
    private val errorBody2 = hasText(resources.getString(R.string.app_signOutErrorBody2))
    private val primaryButton = hasText(resources.getString(R.string.app_SignOutErrorButton))

    private val navigator: Navigator = mock()
    private val viewModel = SignOutErrorViewModel(navigator)

    @Test
    fun signOutErrorScreen() {
        setUp()
        composeTestRule.onNode(errorTitle).assertIsDisplayed()
        composeTestRule.onNode(errorBody1).assertIsDisplayed()
        composeTestRule.onNode(errorBody2).assertIsDisplayed()
        composeTestRule.onNode(primaryButton).assertIsDisplayed()
    }

    @Test
    fun clickExitButton() {
        setUp()
        composeTestRule.onNode(primaryButton).performClick()
        verify(navigator, times(2)).goBack()
    }

    @Test
    fun tapBackButton() {
        setUp()
        Espresso.pressBack()
        verify(navigator, times(2)).goBack()
    }

    @Test
    fun signOutErrorScreenPreview() {
        setUpPreview()
        composeTestRule.onNode(errorTitle).assertIsDisplayed()
        composeTestRule.onNode(errorBody1).assertIsDisplayed()
        composeTestRule.onNode(errorBody2).assertIsDisplayed()
        composeTestRule.onNode(primaryButton).assertIsDisplayed()
    }

    private fun setUp() {
        composeTestRule.setContent {
            SignOutErrorScreen(viewModel)
        }
    }

    private fun setUpPreview() {
        composeTestRule.setContent {
            SignOutWalletPreview()
        }
    }
}
