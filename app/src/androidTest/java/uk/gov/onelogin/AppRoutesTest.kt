package uk.gov.onelogin

import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.onelogin.ext.setupComposeTestRule
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.ui.home.HomeTestRoutes

@HiltAndroidTest
class AppRoutesTest : TestCase() {
    @Inject
    lateinit var appRoutes: IAppRoutes

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun loadingFlowStartsByDefault() {
        navController = composeTestRule.setupComposeTestRule { innerNavController ->
            appRoutes.routes(
                navController = innerNavController,
                startDestination = LoginRoutes.ROOT
            )
        }

        // Race condition as the splash screen moves onto login welcome screen
        val startScreenPresent = navController?.backStack?.any {
            it.destination.route == LoginRoutes.START
        }
        startScreenPresent?.let {
            assertTrue(startScreenPresent)
        } ?: assertEquals(LoginRoutes.WELCOME, navController?.currentDestination?.route)
    }

    @Test
    fun homeFlowIsDeclaredInTheAppRoutes() {
        navController = composeTestRule.setupComposeTestRule { innerNavController ->
            appRoutes.routes(
                navController = innerNavController,
                startDestination = HomeTestRoutes.ROOT
            )
        }

        assertEquals(
            "The home flow's landing screen should be available via 'HomeRoutes.START'!",
            HomeTestRoutes.START,
            navController?.currentDestination?.route
        )
    }
}
