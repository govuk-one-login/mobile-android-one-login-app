package uk.gov.onelogin.signOut.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.signOut.SignOutModule
import uk.gov.onelogin.signOut.domain.SignOutUseCase

@HiltAndroidTest
@UninstallModules(
    SignOutModule::class
)
class SignOutScreenKtTest : TestCase() {

    @BindValue
    val signOutUseCase: SignOutUseCase = mock()

    private val title = hasText(resources.getString(R.string.app_signOutConfirmationTitle))
    private val button = hasText(resources.getString(R.string.app_signOutAndDeleteAppDataButton))

    @Before
    fun setupNavigation() {
        hiltRule.inject()
        composeTestRule.setContent {
            SignOutScreen()
        }
    }

    @Test
    fun verifyScreenDisplayed() {
        composeTestRule.onNode(title).assertIsDisplayed()
    }

    @Test
    fun verifySignOutButton() {
        composeTestRule.onNode(button).performClick()
        verify(signOutUseCase).invoke(composeTestRule.activity)
    }
}
