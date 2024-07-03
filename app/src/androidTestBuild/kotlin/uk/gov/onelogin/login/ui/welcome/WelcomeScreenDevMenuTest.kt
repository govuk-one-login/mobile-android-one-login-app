package uk.gov.onelogin.login.ui.welcome

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class WelcomeScreenDevMenuTest : TestCase() {
    private var devMenuCLicked = 0

    @Before
    fun setupNavigation() {
        hiltRule.inject()
    }
    private val signInIcon =
        hasContentDescription(resources.getString(R.string.app_signInIconDescription))

    @Test
    fun verifyDevMenuClick() {
        composeTestRule.setContent {
            WelcomeScreen(
                openDeveloperPanel = { devMenuCLicked++ }
            )
        }

        composeTestRule.onNode(signInIcon).performClick()

        assertEquals(1, devMenuCLicked)
    }
}
