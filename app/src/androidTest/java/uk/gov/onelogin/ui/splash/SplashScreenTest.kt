package uk.gov.onelogin.ui.splash

import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.ext.setupComposeTestRule
import uk.gov.onelogin.login.ui.splash.SplashScreen

@HiltAndroidTest
class SplashScreenTest : TestCase() {
    @Test
    fun initialisesSplashScreen() {
        composeTestRule.setupComposeTestRule { _ ->
            SplashScreen()
        }
    }
}
