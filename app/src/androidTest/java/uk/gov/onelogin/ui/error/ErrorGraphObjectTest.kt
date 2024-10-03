package uk.gov.onelogin.ui.error

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.MainActivity
import uk.gov.onelogin.e2e.controller.TestCase
import uk.gov.onelogin.navigation.Navigator

@HiltAndroidTest
class ErrorGraphObjectTest : TestCase() {
    @get:Rule(order = 3)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var navigator: Navigator

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun errorGraph_signOutError() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(ErrorRoutes.SignOut)
        }

        composeTestRule.onNodeWithText(resources.getString(R.string.app_signOutErrorTitle))
    }

    @Test
    fun errorGraph_genericError() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(ErrorRoutes.Generic)
        }
        composeTestRule.onNodeWithText(
            resources.getString(R.string.app_somethingWentWrongErrorTitle)
        )
    }

    @Test
    fun errorGraph_offlineError() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            navigator.navigate(ErrorRoutes.Offline)
        }

        composeTestRule.onNodeWithText(resources.getString(R.string.app_networkErrorTitle))
    }
}
