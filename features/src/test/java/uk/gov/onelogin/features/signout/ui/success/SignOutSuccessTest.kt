package uk.gov.onelogin.features.signout.ui.success

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.FragmentActivityTestCase
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class SignOutSuccessTest : FragmentActivityTestCase() {
    private lateinit var navigator: Navigator
    private lateinit var viewModel: SignOutSuccessViewModel

    private val title = hasText(context.getString(R.string.app_signOutTitle))
    private val button = hasText(context.getString(R.string.app_signOutSuccessButton))
    private val bodyWallet = hasText(context.getString(R.string.app_signOutBody))
    private val bodyWallet2 = hasText(context.getString(R.string.app_signOutBody2))

    @Before
    fun setup() {
        navigator = mock()
        viewModel = SignOutSuccessViewModel(navigator)
    }

    @Test
    fun testContentDisplay() {
        setupContent(viewModel)

        composeTestRule.apply {
            onNode(title).assertIsDisplayed()
            onNode(bodyWallet).assertIsDisplayed()
            onNode(bodyWallet2).assertIsDisplayed()
            onNode(button).assertIsDisplayed()
        }
    }

    @Test
    fun onPrimary() {
        setupContent(viewModel)
        composeTestRule.apply {
            onNode(button).assertIsDisplayed().performClick()
        }

        verify(navigator).navigate(LoginRoutes.Root, true)
    }

    @Test
    fun onBackButton() {
        setupContent(viewModel)

        composeTestRule.activityRule.scenario.onActivity { activity ->
            Espresso.pressBack()

            assertTrue(activity.isFinishing)
        }
    }

    @Test
    fun testPreview() {
        composeTestRule.setContent {
            SignOutSuccessPreview()
        }

        composeTestRule.apply {
            onNode(title).assertIsDisplayed()
            onNode(bodyWallet).assertIsDisplayed()
            onNode(bodyWallet2).assertIsDisplayed()
            onNode(button).assertIsDisplayed()
        }
    }

    private fun setupContent(viewModel: SignOutSuccessViewModel) {
        composeTestRule.setContent {
            SignOutSuccess(viewModel)
        }
    }
}
