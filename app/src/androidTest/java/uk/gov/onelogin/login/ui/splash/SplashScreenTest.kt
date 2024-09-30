package uk.gov.onelogin.login.ui.splash

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.state.LocalAuthStatus
import uk.gov.onelogin.login.usecase.HandleLogin
import uk.gov.onelogin.login.usecase.SaveTokens
import uk.gov.onelogin.login.usecase.UseCaseModule
import uk.gov.onelogin.login.usecase.VerifyIdToken
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.navigation.NavigatorModule
import uk.gov.onelogin.optin.ui.NOTICE_TAG

@HiltAndroidTest
@UninstallModules(UseCaseModule::class, NavigatorModule::class)
class SplashScreenTest : TestCase() {
    @BindValue
    val verifyIdToken: VerifyIdToken = mock()

    @BindValue
    val handleLogin: HandleLogin = mock()

    @BindValue
    val mockNavigator: Navigator = mock()

    @BindValue
    val mockSaveTokens: SaveTokens = mock()

    private lateinit var splashIcon: SemanticsMatcher
    private lateinit var unlockButton: SemanticsMatcher
    private lateinit var privacyNotice: SemanticsMatcher

    @Before
    fun setUp() {
        hiltRule.inject()

        splashIcon = hasTestTag(resources.getString(R.string.splashIconTestTag))
        unlockButton = hasText(resources.getString(R.string.app_unlockButton))
        privacyNotice = hasTestTag(NOTICE_TAG)
    }

    @Test
    fun verifySplashScreen() {
        // Given
        composeTestRule.setContent {
            SplashScreen()
        }
        // Then
        composeTestRule.onNode(privacyNotice).assertIsNotDisplayed()
        composeTestRule.onNode(splashIcon).assertIsDisplayed()
    }

    @Test
    fun testUnlockButton() {
        wheneverBlocking { handleLogin.invoke(any(), any()) }.thenAnswer {
            (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.UserCancelled)
        }

        // Given
        composeTestRule.setContent {
            SplashScreen()
        }
        composeTestRule.waitUntil {
            composeTestRule.onNode(unlockButton).isDisplayed()
        }

        wheneverBlocking { handleLogin.invoke(any(), any()) }.thenAnswer {
            (it.arguments[1] as (LocalAuthStatus) -> Unit).invoke(LocalAuthStatus.ManualSignIn)
        }

        // When
        composeTestRule.onNode(unlockButton).performClick()

        // Then
        verify(mockNavigator).goBack()
        verify(mockNavigator).navigate(LoginRoutes.Welcome, false)
    }
}
