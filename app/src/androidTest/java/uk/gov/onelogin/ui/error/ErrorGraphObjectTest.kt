package uk.gov.onelogin.ui.error

import androidx.test.core.app.launchActivity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.gov.android.onelogin.BuildConfig
import uk.gov.android.onelogin.R
import uk.gov.onelogin.FlakyTestRule
import uk.gov.onelogin.MainActivity
import uk.gov.onelogin.e2e.controller.TestCase
import uk.gov.onelogin.navigation.Navigator

@HiltAndroidTest
class ErrorGraphObjectTest : TestCase() {
    @get:Rule(order = 3)
    val flakyTestRule = FlakyTestRule()

    @Inject
    lateinit var navigator: Navigator

    @Before
    fun setup() {
        hiltRule.inject()
        launchActivity<MainActivity>()
    }

    @Test
    fun errorGraph_signOutError() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(ErrorRoutes.SignOut)
        }

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_signOutErrorTitle))
        )
        phoneController.click(
            selectors = arrayOf(
                Pair(By.text(resources.getString(R.string.app_exitButton)), "Exit button")
            )
        )
        assertNotEquals(phoneController.getCurrentPackage(), BuildConfig.APPLICATION_ID)
    }

    @Test
    fun errorGraph_genericError() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(ErrorRoutes.Generic)
        }

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_somethingWentWrongErrorTitle))
        )
        phoneController.click(
            selectors = arrayOf(
                Pair(By.text(resources.getString(R.string.app_closeButton)), "Close button")
            )
        )
        phoneController.waitUntilIdle()
        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_analyticsPermissionTitle))
        )
    }

    @Test
    fun errorGraph_offlineError() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(ErrorRoutes.Offline)
        }

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_networkErrorTitle))
        )
        phoneController.click(
            selectors = arrayOf(
                Pair(By.text(resources.getString(R.string.app_tryAgainButton)), "Try again button")
            )
        )
        phoneController.waitUntilIdle()
        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_analyticsPermissionTitle))
        )
    }
}
