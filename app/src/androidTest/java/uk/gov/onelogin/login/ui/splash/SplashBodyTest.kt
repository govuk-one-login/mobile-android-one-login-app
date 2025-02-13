package uk.gov.onelogin.login.ui.splash

import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.core.analytics.AnalyticsModule

@HiltAndroidTest
@UninstallModules(AnalyticsModule::class)
class SplashBodyTest : TestCase() {
    @BindValue
    var analytics: AnalyticsLogger = mock()

    private lateinit var splashIcon: SemanticsMatcher
    private lateinit var unlockButton: SemanticsMatcher
    private lateinit var loadingText: SemanticsMatcher
    private lateinit var loadingIndicator: SemanticsMatcher

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val resources: Resources = context.resources

        splashIcon = hasTestTag(resources.getString(R.string.splashIconTestTag))
        unlockButton = hasText(resources.getString(R.string.app_unlockButton))
        loadingText = hasText(resources.getString(R.string.app_splashScreenLoadingIndicatorText))
        loadingIndicator = hasContentDescription(
            resources.getString(R.string.app_splashScreenLoadingContentDescription)
        )
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
        // And both `splashIcon` and `unlockButton` are displayed
        composeTestRule.onNode(splashIcon).assertIsDisplayed()
        composeTestRule.onNode(unlockButton).assertIsDisplayed()
        verify(analytics).logEventV3Dot1(
            SplashScreenAnalyticsViewModel.makeScreenEvent(
                context,
                true
            )
        )
    }

    @Test
    fun verifyLock() {
        val unlock = false
        // Given the SplashBody Composable with `isUnlock` set to false
        composeTestRule.setContent {
            SplashBody(
                isUnlock = unlock,
                loading = true,
                onLogin = {},
                trackUnlockButton = {},
                onOpenDeveloperPortal = {}
            )
        }
        // Then loading indicator is displayed
        composeTestRule.onNode(loadingText).assertIsDisplayed()
        composeTestRule.onNode(loadingIndicator).assertIsDisplayed()
        // And only `splashIcon` is displayed and`unlockButton` is not
        composeTestRule.onNode(splashIcon).assertIsDisplayed()
        composeTestRule.onNode(unlockButton).assertIsNotDisplayed()
        verify(analytics).logEventV3Dot1(
            SplashScreenAnalyticsViewModel.makeScreenEvent(
                context,
                unlock
            )
        )
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
        composeTestRule.apply {
            onNode(unlockButton).performClick()
            // Test system back button to trigger analytics event being logged
            activityRule.scenario.onActivity { activity ->
                activity.onBackPressedDispatcher.onBackPressed()
            }
        }
        // Then onLogin() is called and the variable is changed to true
        assertTrue(actual)
        assertTrue(buttonLogged)
        verify(analytics).logEventV3Dot1(SplashScreenAnalyticsViewModel.makeButtonEvent(context))
        verify(analytics).logEventV3Dot1(
            SplashScreenAnalyticsViewModel.makeBackEvent(context, unlock)
        )
    }

    @Test
    fun onOpenDeveloperPortal() {
        // Given the SplashBody Composable
        var actual = false
        composeTestRule.setContent {
            SplashBody(
                isUnlock = false,
                loading = false,
                trackUnlockButton = { },
                onLogin = {},
                onOpenDeveloperPortal = { actual = true }
            )
        }
        // When clicking the `splashIcon`
        composeTestRule.onNode(splashIcon).performClick()
        // Then onOpenDeveloperPortal() is called and the variable is changed to true
        assertTrue(actual)
    }
}
