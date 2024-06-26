package uk.gov.onelogin.login.ui.splash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.login.state.LocalAuthStatus
import uk.gov.onelogin.login.usecase.HandleLogin
import uk.gov.onelogin.login.usecase.UseCaseModule
import uk.gov.onelogin.login.usecase.VerifyIdToken

@HiltAndroidTest
@UninstallModules(UseCaseModule::class)
class SplashScreenTest : TestCase() {
    @BindValue
    val verifyIdToken: VerifyIdToken = mock()

    @BindValue
    val handleLogin: HandleLogin = mock()

    private val splashIcon = hasTestTag(resources.getString(R.string.splashIconTestTag))
    private val unlockButton = hasText(resources.getString(R.string.app_unlockButton))

    private var nextScreen: Int = 0

    @Before
    fun setup() = runTest {
        nextScreen = 0

        hiltRule.inject()
        whenever(handleLogin.invoke(any(), any())).thenAnswer {
            (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.UserCancelled)
        }

        composeTestRule.setContent {
            SplashScreen(
                nextScreen = { nextScreen++ }
            )
        }
    }

    @Test
    fun verifySplashScreen() {
        composeTestRule.onNode(splashIcon).assertIsDisplayed()
    }

    @Test
    fun testUnlockButton() = runTest {
        composeTestRule.onNode(unlockButton).assertIsDisplayed()
        whenever(handleLogin.invoke(any(), any())).thenAnswer {
            (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.ManualSignIn)
        }
        composeTestRule.onNode(unlockButton).performClick()

        assertEquals(1, nextScreen)
    }
}
