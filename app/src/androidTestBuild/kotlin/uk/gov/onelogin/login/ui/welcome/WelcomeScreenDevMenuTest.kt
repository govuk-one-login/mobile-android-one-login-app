package uk.gov.onelogin.login.ui.welcome

import androidx.compose.ui.test.hasContentDescription
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
class WelcomeScreenDevMenuTest : TestCase() {
    @BindValue
    val mockNavigator: Navigator = mock()

    @Before
    fun setupNavigation() {
        hiltRule.inject()
    }

    private val signInIcon =
        hasContentDescription(resources.getString(R.string.app_signInIconDescription))

    @Test
    fun verifyDevMenuClick() {
        composeTestRule.setContent {
            WelcomeScreen()
        }

        composeTestRule.onNode(signInIcon).performClick()

        verify(mockNavigator).openDeveloperPanel()
    }
}
