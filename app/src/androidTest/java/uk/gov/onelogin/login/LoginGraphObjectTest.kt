package uk.gov.onelogin.login

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
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
    @get:Rule(order = 3)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Inject
    lateinit var navigator: Navigator

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun loginGraph_SignInError() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(LoginRoutes.SignInError)
        }

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_signInErrorTitle))
        )
    }

    @Test
    fun loginGraph_BioOptInScreen() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(LoginRoutes.BioOptIn)
        }

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_enableBiometricsTitle))
        )
        phoneController.pressBack()
        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_enableBiometricsTitle))
        )
    }

    @Test
    fun loginGraph_AnalyticsOptInScreen() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(LoginRoutes.AnalyticsOptIn)
        }

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_analyticsPermissionBody))
        )
        phoneController.pressBack()
        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_analyticsPermissionBody))
        )
    }

    @Test
    fun loginGraph_PasscodeInfo_Button() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(LoginRoutes.PasscodeInfo)
        }

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_noPasscodePatternSetupTitle))
        )
        phoneController.click(
            selectors = arrayOf(
                Pair(By.text(resources.getString(R.string.app_continue)), "Continue button")
            )
        )
        phoneController.waitUntilIdle()
        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_homeTitle))
        )
    }

    @Test
    fun loginGraph_Loading() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(LoginRoutes.Loading)
        }
    }
}
