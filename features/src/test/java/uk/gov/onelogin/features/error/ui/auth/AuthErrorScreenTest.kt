package uk.gov.onelogin.features.error.ui.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class AuthErrorScreenTest : FragmentActivityTestCase() {
    private lateinit var viewModel: AuthErrorViewModel
    private val navigator: Navigator = mock()

    private val title = hasText(resources.getString(R.string.app_dataDeletedErrorTitle))
    private val body1 = hasText(resources.getString(R.string.app_dataDeletedBody1))
    private val body2 = hasText(resources.getString(R.string.app_dataDeletedBody2))
    private val body3 = hasText(resources.getString(R.string.app_dataDeletedBody3))
    private val primary = hasText(resources.getString(R.string.app_dataDeletedButton))

    @Test
    fun reAuthErrorScreen() {
        viewModel = AuthErrorViewModel(navigator)
        composeTestRule.setContent {
            AuthErrorScreen(viewModel)
        }

        composeTestRule.apply {
            onNode(title).assertIsDisplayed()
            onNode(body1).assertIsDisplayed()
            onNode(body2).assertIsDisplayed()
            onNode(body3).assertIsDisplayed()
            onNode(primary).assertIsDisplayed()
        }
    }

    @Test
    fun onPrimary() {
        viewModel = AuthErrorViewModel(navigator)
        composeTestRule.setContent {
            AuthErrorScreen(viewModel)
        }

        composeTestRule.onNode(primary)
            .performClick()

        verify(navigator).navigate(LoginRoutes.Start, true)
    }

    @Test
    fun preview() {
        composeTestRule.setContent {
            AuthErrorScreenPreview()
        }
    }
}
