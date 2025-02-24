package uk.gov.onelogin.features.error.ui.reauth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.TestCase

class ReAuthErrorScreenTest : TestCase() {
    private lateinit var viewModel: ReAuthErrorViewModel
    private val mockNavigator: Navigator = mock()
    private val icon =
        hasContentDescription(
            resources.getString(R.string.app_dataDeletedError_ContentDescription)
        )
    private val title = hasText(resources.getString(R.string.app_dataDeletedErrorTitle))
    private val intro = hasText(resources.getString(R.string.app_dataDeletedErrorBody1))
    private val content = hasText(resources.getString(R.string.app_dataDeletedErrorBody2))
    private val bulletItem1 = hasText(resources.getString(R.string.app_dataDeletedErrorBullet1))
    private val bulletItem2 = hasText(resources.getString(R.string.app_dataDeletedErrorBullet2))
    private val bulletItem3 = hasText(resources.getString(R.string.app_dataDeletedErrorBullet3))
    private val instruction = hasText(resources.getString(R.string.app_dataDeletedErrorBody3))
    private val primary = hasText(resources.getString(R.string.app_SignInWithGovUKOneLoginButton))

    @Before
    fun setup() {
        viewModel = ReAuthErrorViewModel(mockNavigator)
        composeTestRule.setContent {
            ReAuthErrorScreen(viewModel)
        }
    }

    @Test
    fun reAuthErrorScreen() {
        composeTestRule.apply {
            onNode(icon).assertIsDisplayed()
            onNode(title).assertIsDisplayed()
            onNode(intro).assertIsDisplayed()
            onNode(content).assertIsDisplayed()
            onNode(bulletItem1).assertIsDisplayed()
            onNode(bulletItem2).assertIsDisplayed()
            onNode(bulletItem3).assertIsDisplayed()
            onNode(instruction).assertIsDisplayed()
        }
    }

    @Test
    fun onPrimary() {
        composeTestRule.onNode(primary)
            .performClick()

        verify(mockNavigator).navigate(LoginRoutes.Start, true)
    }
}
