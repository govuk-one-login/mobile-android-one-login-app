package uk.gov.onelogin.features.login.ui.splash

import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.login.ui.signin.splash.SplashBody

@RunWith(AndroidJUnit4::class)
class SplashBodyTest : FragmentActivityTestCase() {
    private lateinit var logo: SemanticsMatcher
    private lateinit var crownIcon: SemanticsMatcher
    private lateinit var unlockButton: SemanticsMatcher
    private lateinit var loadingText: SemanticsMatcher
    private lateinit var loadingIndicator: SemanticsMatcher
    private lateinit var loadingContentDescription: SemanticsMatcher

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val resources: Resources = context.resources

        logo = hasTestTag(context.getString(R.string.splashLogoTestTag))
        crownIcon = hasTestTag(resources.getString(R.string.splashCrownIconTestTag))
        unlockButton = hasText(resources.getString(R.string.app_unlockButton))
        loadingText = hasText(resources.getString(R.string.app_splashScreenLoadingIndicatorText))
        loadingContentDescription =
            hasContentDescription(
                resources.getString(R.string.app_loading_content_desc)
            )
        loadingIndicator = hasTestTag(context.getString(R.string.splashLoadingSpinnerTestTag))
    }

    @Test
    fun verifyUnlock() {
        // Given the SplashBody Composable with `isUnlock` set to true
        composeTestRule.setContent {
            SplashBody(
                isUnlock = true,
                loading = false,
                onLogin = {},
                trackUnlockButton = {},
                onOpenDeveloperPortal = {}
            )
        }
        // Then loading indicator is NOT displayed
        composeTestRule.onNode(loadingText).assertIsNotDisplayed()
        composeTestRule.onNode(loadingIndicator).assertIsNotDisplayed()
        // And `logo`, `crownIcon` and `unlockButton` are displayed
        composeTestRule.onNode(logo).assertIsDisplayed()
        composeTestRule.onNode(crownIcon).assertIsDisplayed()
        composeTestRule.onNode(unlockButton).assertExists()
    }

    @Test
    fun verifyLock() {
        // Given the SplashBody Composable with `isUnlock` set to false
        composeTestRule.setContent {
            SplashBody(
                isUnlock = false,
                loading = true,
                onLogin = {},
                trackUnlockButton = {},
                onOpenDeveloperPortal = {}
            )
        }
        // Then loading indicator and logo are displayed
        composeTestRule.onNode(logo).assertIsDisplayed()
        composeTestRule.onAllNodes(loadingText).assertCountEquals(2)
        composeTestRule.onAllNodes(loadingIndicator).assertCountEquals(2)
        composeTestRule.onAllNodes(loadingContentDescription).assertCountEquals(2)
        // And only `logo`, `crownIcon` is displayed and`unlockButton` is not
        composeTestRule.onNode(logo).assertIsDisplayed()
        composeTestRule.onNode(crownIcon).assertIsDisplayed()
        composeTestRule.onNode(unlockButton).assertIsNotDisplayed()
    }

    @Test
    fun onLogin() {
        // Given the SplashBody Composable
        val unlock = true
        var actual = false
        var buttonLogged = false
        composeTestRule.setContent {
            SplashBody(
                isUnlock = unlock,
                loading = false,
                trackUnlockButton = { buttonLogged = true },
                onLogin = { actual = true },
                onOpenDeveloperPortal = {}
            )
        }
        // When clicking the `unlockButton`
        composeTestRule.onNode(unlockButton).performClick()

        // Then onLogin() is called and the variable is changed to true
        assertTrue(actual)
        assertTrue(buttonLogged)
    }
}
