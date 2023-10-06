package uk.gov.onelogin

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.onelogin.login.LoginTestRoutes

@RunWith(AndroidJUnit4::class)
class AppRoutesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupNavigation() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            AppRoutes(navController)
        }
    }

    @Test
    fun loginFlowStartsByDefault() {
        assertEquals(
            "The default destination for app routes should have been 'LoginRoutes.START'!",
            LoginTestRoutes.START,
            navController.currentDestination?.route,
        )
    }
}
