package uk.gov.onelogin

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import uk.gov.onelogin.ext.setupComposeTestRule
import uk.gov.onelogin.home.HomeTestRoutes
import uk.gov.onelogin.login.LoginTestRoutes
import javax.inject.Inject

@HiltAndroidTest
class AppRoutesTest @Inject constructor(
    private val appRoutes: IAppRoutes
) {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Test
    fun loadingFlowStartsByDefault() {
        navController = composeTestRule.setupComposeTestRule { innerNavController ->
            appRoutes.routes(
                navController = innerNavController,
                startDestination = LoginTestRoutes.START
            )
        }

        assertEquals(
            "The default destination for app routes should have been 'LoginRoutes.START'!",
            LoginTestRoutes.LOADING,
            navController.currentDestination?.route
        )
    }

    @Test
    @Ignore("We need to be able to inject stubs/mocks before this test can work")
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
            navController.currentDestination?.route
        )
    }
}
