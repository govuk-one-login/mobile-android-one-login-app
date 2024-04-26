package uk.gov.onelogin

import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.onelogin.ext.setupComposeTestRule
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.mainnav.nav.MainNavRoutes
import uk.gov.onelogin.ui.error.ErrorRoutes

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

        assertEquals(LoginRoutes.WELCOME, navController?.currentDestination?.route)
    }

    @Test
    fun mainNavIsDeclaredInTheAppRoutes() {
        navController = composeTestRule.setupComposeTestRule { innerNavController ->
            appRoutes.routes(
                navController = innerNavController,
                startDestination = MainNavRoutes.ROOT
            )
        }

        assertEquals(
            "The home flow's landing screen should be available via 'MainNavRoutes.START'!",
            MainNavRoutes.START,
            navController?.currentDestination?.route
        )
    }

    @Test
    fun errorRoutesStartsByDefault() {
        navController = composeTestRule.setupComposeTestRule { innerNavController ->
            appRoutes.routes(
                navController = innerNavController,
                startDestination = ErrorRoutes.ROOT
            )
        }

        assertEquals(ErrorRoutes.GENERIC, navController?.currentDestination?.route)
    }
}
