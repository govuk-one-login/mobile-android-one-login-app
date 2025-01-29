package uk.gov.onelogin.ui.home

import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.ext.setupComposeTestRule

@HiltAndroidTest
class HomeScreenKtTest : TestCase() {
    @Test
    fun initialisesHomeScreen() {
        composeTestRule.setupComposeTestRule { _ ->
            HomeScreen()
        }
    }
}
