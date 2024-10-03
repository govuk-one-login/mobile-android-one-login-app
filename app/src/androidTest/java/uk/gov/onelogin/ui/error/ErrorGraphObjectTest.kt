package uk.gov.onelogin.ui.error

import androidx.test.core.app.launchActivity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.MainActivity
import uk.gov.onelogin.e2e.controller.TestCase
import uk.gov.onelogin.navigation.Navigator

@HiltAndroidTest
class ErrorGraphObjectTest : TestCase() {
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
    }

    @Test
    fun errorGraph_genericError() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(ErrorRoutes.Generic)
        }
        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_somethingWentWrongErrorTitle))
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
    }
}
