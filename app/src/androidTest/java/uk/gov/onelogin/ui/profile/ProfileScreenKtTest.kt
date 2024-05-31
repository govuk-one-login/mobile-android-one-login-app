package uk.gov.onelogin.ui.profile

import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.ext.setupComposeTestRule

@HiltAndroidTest
class ProfileScreenKtTest : TestCase() {

    @Test
    fun initialisesHomeScreenProfileScreen() {
        composeTestRule.setupComposeTestRule { _ ->
            ProfileScreen()
        }
    }
}
