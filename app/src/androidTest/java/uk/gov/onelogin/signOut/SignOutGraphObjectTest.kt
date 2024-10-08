package uk.gov.onelogin.signOut

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
class SignOutGraphObjectTest : TestCase() {
    @Inject
    lateinit var navigator: Navigator

    @Before
    fun setup() {
        hiltRule.inject()
        launchActivity<MainActivity>()
    }

    @Test
    fun signOutGraph_startingDestination() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(SignOutRoutes.Start)
        }

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_signOutConfirmationTitle))
        )
    }

    @Test
    fun signOutGraph_navigateToSignedOutInfoScreen() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(SignOutRoutes.Info)
        }

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_youveBeenSignedOutTitle))
        )
    }

    @Test
    fun signOutGraph_navigateToReAuthErrorScreen() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(SignOutRoutes.ReAuthError)
        }

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_dataDeletedErrorTitle))
        )

        phoneController.pressBack()
        phoneController.navigateToApp(packageName = "uk.gov.android.onelogin")

        phoneController.assertElementExists(
            selector = By.text(resources.getString(R.string.app_dataDeletedErrorTitle))
        )
    }
}
