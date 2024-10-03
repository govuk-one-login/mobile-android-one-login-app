package uk.gov.onelogin.login.ui.splash

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase

@HiltAndroidTest
class SplashScreenDevMenuTest : TestCase() {

    private val splashIcon = hasTestTag(resources.getString(R.string.splashIconTestTag))

    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.setContent {
            SplashScreen()
        }
    }

    @Test
    fun testDevMenuButton() {
        composeTestRule.onNode(splashIcon).performClick()

        composeTestRule.onNodeWithText("Developer Portal").assertDoesNotExist()
    }
}
