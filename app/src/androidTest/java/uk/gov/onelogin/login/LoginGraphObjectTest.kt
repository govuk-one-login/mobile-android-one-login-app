package uk.gov.onelogin.login

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.FlakyTest
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.MainActivity
import uk.gov.onelogin.e2e.controller.TestCase
import uk.gov.onelogin.navigation.Navigator

@HiltAndroidTest
class LoginGraphObjectTest : TestCase() {
    @get:Rule(order = 4)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var navigator: Navigator

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @FlakyTest
    @Test
    fun loginGraph_SignInError() {
        composeTestRule.activityRule.scenario.onActivity {
            navigator.navigate(LoginRoutes.SignInError)
        }

        composeTestRule.onNodeWithText(resources.getString(R.string.app_signInErrorTitle))
    }

    @FlakyTest
    @Test
    fun loginGraph_BioOptInScreen() {
        composeTestRule.activityRule.scenario.onActivity {
            navigator.navigate(LoginRoutes.BioOptIn)
        }

        composeTestRule.onNodeWithText(resources.getString(R.string.app_enableBiometricsTitle))
        back()
        composeTestRule.onNodeWithText(resources.getString(R.string.app_enableBiometricsTitle))
    }

    @FlakyTest
    @Test
    fun loginGraph_AnalyticsOptInScreen() {
        composeTestRule.activityRule.scenario.onActivity {
            navigator.navigate(LoginRoutes.AnalyticsOptIn)
        }

        composeTestRule.onNodeWithText(resources.getString(R.string.app_analyticsPermissionBody))
        back()
        composeTestRule.onNodeWithText(resources.getString(R.string.app_analyticsPermissionBody))
    }

    @FlakyTest
    @Test
    fun loginGraph_PasscodeInfo_Button() {
        composeTestRule.activityRule.scenario.onActivity {
            navigator.navigate(LoginRoutes.PasscodeInfo)
        }

        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_noPasscodePatternSetupTitle)
        )
        composeTestRule.onNodeWithText(resources.getString(R.string.app_continue)).performClick()
        composeTestRule.onNodeWithText(resources.getString(R.string.app_homeTitle))
    }

    @FlakyTest
    @Test
    fun loginGraph_Loading() {
        composeTestRule.activityRule.scenario.onActivity {
            navigator.navigate(LoginRoutes.Loading)
        }
    }

    private fun back() {
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
    }
}
