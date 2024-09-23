package uk.gov.onelogin.signOut.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.onelogin.R
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.navigation.NavigatorModule
import uk.gov.onelogin.signOut.SignOutModule
import uk.gov.onelogin.signOut.domain.SignOutError
import uk.gov.onelogin.signOut.domain.SignOutUseCase
import uk.gov.onelogin.ui.error.ErrorRoutes
import uk.gov.onelogin.wallet.DeleteWalletDataUseCase
import uk.gov.onelogin.wallet.WalletModule

@HiltAndroidTest
@UninstallModules(
    SignOutModule::class,
    NavigatorModule::class,
    WalletModule::class
)
class SignOutScreenTest : TestCase() {
    @BindValue
    val mockNavigator: Navigator = mock()

    @BindValue
    val signOutUseCase: SignOutUseCase = mock()

    @BindValue
    val walletSdk: WalletSdk = mock()

    @BindValue
    val deleteWalletDataUseCase: DeleteWalletDataUseCase = mock()

    private val title = hasText(resources.getString(R.string.app_signOutConfirmationTitle))
    private val ctaButton = hasText(resources.getString(R.string.app_signOutAndDeleteAppDataButton))
    private val goBackButton = hasContentDescription("Close")

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
    fun verifySignOutButtonSucceeds() = runBlocking {
        composeTestRule.onNode(ctaButton).performClick()
        verify(signOutUseCase).invoke(any())
        verify(mockNavigator).navigate(LoginRoutes.Root, true)
    }

    @Test(expected = Throwable::class)
    fun verifySignOutButtonFails() = runTest {
        whenever(signOutUseCase.invoke(any()))
            .thenThrow(SignOutError(Exception("something went wrong")))
        composeTestRule.onNode(ctaButton).performClick()
        verify(signOutUseCase).invoke(any())
        verify(mockNavigator).navigate(ErrorRoutes.SignOut)
    }

    @Test
    fun verifyGoBackButton() {
        composeTestRule.onNode(goBackButton).performClick()
        verify(mockNavigator).goBack()
    }
}
