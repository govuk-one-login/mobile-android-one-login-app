package uk.gov.onelogin.login.ui.splash

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.navigation.NavigatorModule

@HiltAndroidTest
@UninstallModules(NavigatorModule::class)
class SplashScreenDevMenuTest : TestCase() {
    @BindValue
    val mockNavigator: Navigator = mock()

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

        verify(mockNavigator).openDeveloperPanel()
    }
}
