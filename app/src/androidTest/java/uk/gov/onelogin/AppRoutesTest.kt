package uk.gov.onelogin

import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Assert.assertEquals
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

        assertEquals(
            "The default destination for app routes should have been 'LoginRoutes.START'!",
            LoginRoutes.START,
            navController?.currentDestination?.route
        )
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
