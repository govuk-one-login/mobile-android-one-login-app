package uk.gov.onelogin.login.ui.splash

import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.gov.android.onelogin.R

@HiltAndroidTest
class SplashBodyTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var splashIcon: SemanticsMatcher
    private lateinit var unlockButton: SemanticsMatcher

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val resources: Resources = context.resources

        splashIcon = hasTestTag(resources.getString(R.string.splashIconTestTag))
        unlockButton = hasText(resources.getString(R.string.app_unlockButton))
    }

    @Test
    fun verifyUnlock() {
        // Given the SplashBody composable with isUnlock set to true
        composeTestRule.setContent {
            SplashBody(
                isUnlock = true,
                onLogin = {},
                onOpenDeveloperPortal = {}
            )
        }
        // Then both splashIcon and unlockButton are displayed
        composeTestRule.onNode(splashIcon).assertIsDisplayed()
        composeTestRule.onNode(unlockButton).assertIsDisplayed()
    }

    @Test
    fun verifyLock() {
        // Given the SplashBody composable with isUnlock set to false
        composeTestRule.setContent {
            SplashBody(
                isUnlock = false,
                onLogin = {},
                onOpenDeveloperPortal = {}
            )
        }
        // Then only splashIcon is displayed and unlockButton is not
        composeTestRule.onNode(splashIcon).assertIsDisplayed()
        composeTestRule.onNode(unlockButton).assertIsNotDisplayed()
    }

    @Test
    fun onLogin() {
        // Given the SplashBody composable
        var actual = false
        composeTestRule.setContent {
            SplashBody(
                isUnlock = true,
                onLogin = { actual = true },
                onOpenDeveloperPortal = {}
            )
        }
        // When clicking the unlockButton
        composeTestRule.onNode(unlockButton).performClick()
        // Then onLogin is called and the variable is changed to true
        assertEquals(true, actual)
    }

    @Test
    fun onOpenDeveloperPortal() {
        // Given the SplashBody composable
        var actual = false
        composeTestRule.setContent {
            SplashBody(
                isUnlock = false,
                onLogin = {},
                onOpenDeveloperPortal = { actual = true }
            )
        }
        // When clicking the splashIcon
        composeTestRule.onNode(splashIcon).performClick()
        // Then onOpenDeveloperPortal is called and the variable is changed to true
        assertEquals(true, actual)
    }
}
