package uk.gov.onelogin

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.gov.onelogin.ext.setupComposeTestRule
import uk.gov.onelogin.home.HomeTestRoutes
import uk.gov.onelogin.login.LoginRoutes
import javax.inject.Inject

@HiltAndroidTest
class AppRoutesTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Inject
    lateinit var appRoutes: IAppRoutes

    @Before
    fun setup() {
        hiltTestRule.inject()
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
            LoginRoutes.LOADING,
            navController.currentDestination?.route
        )
    }

    //    @Ignore("We need to be able to inject stubs/mocks before this test can work")
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
            navController.currentDestination?.route
        )
    }
}
