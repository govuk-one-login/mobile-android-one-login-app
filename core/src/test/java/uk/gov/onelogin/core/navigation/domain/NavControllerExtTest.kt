package uk.gov.onelogin.core.navigation.domain

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.onelogin.core.FragmentActivityTestCase
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.NavControllerExtTest.LoginGraphObject.loginGraphTest

@RunWith(AndroidJUnit4::class)
class NavControllerExtTest : FragmentActivityTestCase() {
    @Before
    fun setUp() {
        composeTestRule.setContent {
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(
                navController = navController,
                startDestination = LoginRoutes.Root.getRoute()
            ) { loginGraphTest() }
        }
    }

    @Test
    fun hasPreviousBackStackFalseInitially() {
        assertFalse(navController.hasPreviousBackStack())
    }

    @Test
    fun closeAppPopsAllBackStack() {
        composeTestRule.runOnUiThread {
            // Fill up backstack
            navController.setCurrentDestination(LoginRoutes.Loading.getRoute())
            navController.setCurrentDestination(LoginRoutes.SignInRecoverableError.getRoute())
            navController.setCurrentDestination(LoginRoutes.AnalyticsOptIn.getRoute())
            navController.closeApp()
        }
        assertFalse(navController.hasPreviousBackStack())
    }

    internal object LoginGraphObject {
        @Suppress("LongMethod")
        fun NavGraphBuilder.loginGraphTest() {
            navigation(
                route = LoginRoutes.Root.getRoute(),
                startDestination = LoginRoutes.Start.getRoute()
            ) {
                composable(route = LoginRoutes.Start.getRoute()) {}

                composable(route = LoginRoutes.Welcome.getRoute()) {}

                composable(route = LoginRoutes.Loading.getRoute()) {}

                composable(route = LoginRoutes.SignInRecoverableError.getRoute()) {}

                composable(route = LoginRoutes.AnalyticsOptIn.getRoute()) {}
            }
        }
    }
}
