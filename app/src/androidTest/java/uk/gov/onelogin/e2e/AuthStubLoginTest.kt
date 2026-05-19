package uk.gov.onelogin.e2e

import androidx.test.filters.FlakyTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.BuildConfig
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.e2e.controller.TestCase

@HiltAndroidTest
class AuthStubLoginTest : TestCase() {
    private val device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    @FlakyTest
    fun loginViaAuthStubReturnsToApp() {
        phoneController.navigateToApp(
            actionTimeoutOverride = LONG_TIMEOUT,
            packageName = BuildConfig.APPLICATION_ID,
        )

        phoneController.optionalClick(
            actionTimeoutOverride = SHORT_TIMEOUT,
            By.text(resources.getString(R.string.app_doNotShareAnalytics)) to
                "Opting out of analytics",
        )

        phoneController.click(
            actionTimeoutOverride = LONG_TIMEOUT,
            By.text(resources.getString(R.string.app_signInButton)) to
                "Clicking app sign-in button",
        )

        phoneController.optionalClick(
            actionTimeoutOverride = LONG_TIMEOUT,
            By.text("Reject additional cookies") to
                "Rejecting browser cookies",
        )

        phoneController.optionalClick(
            actionTimeoutOverride = LONG_TIMEOUT,
            By.text(resources.getString(R.string.app_signInButton)) to
                "Clicking browser sign-in button",
        )

        phoneController.assertElementExists(
            actionTimeoutOverride = LONG_TIMEOUT,
            selector = By.textContains("Welcome to the Auth Stub"),
        )

        phoneController.click(
            actionTimeoutOverride = LONG_TIMEOUT,
            By.text("Login") to "Clicking Auth Stub login button",
        )

        phoneController.click(
            actionTimeoutOverride = LONG_TIMEOUT,
            By.text("Continue") to "Clicking browser continue button",
        )

        assertTrue(
            "Expected auth stub login to return to the app",
            device.wait(
                Until.hasObject(By.pkg(BuildConfig.APPLICATION_ID).depth(0)),
                LONG_TIMEOUT,
            ),
        )
    }

    companion object {
        private const val SHORT_TIMEOUT = 5_000L
        private const val LONG_TIMEOUT = 30_000L
    }
}
