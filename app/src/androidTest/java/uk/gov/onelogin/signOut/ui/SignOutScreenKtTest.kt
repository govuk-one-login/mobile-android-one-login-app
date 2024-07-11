package uk.gov.onelogin.signOut.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.signOut.SignOutModule
import uk.gov.onelogin.signOut.domain.SignOutError
import uk.gov.onelogin.signOut.domain.SignOutUseCase

@HiltAndroidTest
@UninstallModules(
    SignOutModule::class
)
class SignOutScreenKtTest : TestCase() {

    @BindValue
    val signOutUseCase: SignOutUseCase = mock()
    private val goBack: () -> Unit = mock()
    private val signIn: () -> Unit = mock()
    private val goToSignOutError: () -> Unit = mock()

    private val title = hasText(resources.getString(R.string.app_signOutConfirmationTitle))
    private val ctaButton = hasText(resources.getString(R.string.app_signOutAndDeleteAppDataButton))
    private val goBackButton = hasContentDescription("Close")

    @Before
    fun setupNavigation() {
        hiltRule.inject()
        composeTestRule.setContent {
            SignOutScreen(goBack, signIn, goToSignOutError)
        }
    }

    @Test
    fun verifyScreenDisplayed() {
        composeTestRule.onNode(title).assertIsDisplayed()
    }

    @Test
    fun verifySignOutButtonSucceeds() {
        composeTestRule.onNode(ctaButton).performClick()
        verify(signOutUseCase).invoke(composeTestRule.activity)
        verify(signIn).invoke()
    }

    @Test
    fun verifySignOutButtonFails() {
        whenever(signOutUseCase.invoke(composeTestRule.activity))
            .thenThrow(SignOutError(Exception("something went wrong")))
        composeTestRule.onNode(ctaButton).performClick()
        verify(signOutUseCase).invoke(composeTestRule.activity)
        verify(signIn, never()).invoke()
        verify(goToSignOutError).invoke()
    }

    @Test
    fun verifyGoBackButton() {
        composeTestRule.onNode(goBackButton).performClick()
        verify(goBack).invoke()
    }
}
