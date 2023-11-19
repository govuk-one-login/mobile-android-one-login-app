package uk.gov.onelogin

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.testing.TestNavHostController
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import uk.gov.onelogin.ext.setupComposeTestRule
import uk.gov.onelogin.home.HomeTestRoutes
import uk.gov.onelogin.login.LoginTestRoutes

class AppRoutesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Test
    fun loginFlowStartsByDefault() {
        navController = composeTestRule.setupComposeTestRule { innerNavController ->
            AppRoutes(innerNavController)
        }

        assertEquals(
            "The default destination for app routes should have been 'LoginRoutes.START'!",
            LoginTestRoutes.START,
            navController.currentDestination?.route
        )
    }

    @Test
    fun homeFlowIsDeclaredInTheAppRoutes() {
        navController = composeTestRule.setupComposeTestRule { innerNavController ->
            AppRoutes(innerNavController, startDestination = HomeTestRoutes.ROOT)
        }

        assertEquals(
            "The home flow's landing screen should be available via 'HomeRoutes.START'!",
            HomeTestRoutes.START,
            navController.currentDestination?.route
        )
    }
}
