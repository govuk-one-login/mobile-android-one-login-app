package uk.gov.onelogin.login.ui.splash

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class SplashScreenDevMenuTest : TestCase() {

    private val splashIcon = hasTestTag(resources.getString(R.string.splashIconTestTag))
    private var openDeveloperPanel: Int = 0

    @Before
    fun setup() {
        openDeveloperPanel = 0

        hiltRule.inject()
        composeTestRule.setContent {
            SplashScreen(
                openDeveloperPanel = { openDeveloperPanel++ }
            )
        }
    }

    @Test
    fun testDevMenuButton() {
        composeTestRule.onNode(splashIcon).performClick()

        assertEquals(1, openDeveloperPanel)
    }
}
