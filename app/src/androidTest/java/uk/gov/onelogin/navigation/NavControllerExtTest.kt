package uk.gov.onelogin.navigation

import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.closeApp
import uk.gov.onelogin.core.navigation.domain.hasPreviousBackStack
import uk.gov.onelogin.navigation.graphs.LoginGraphObject.loginGraph
import uk.gov.onelogin.utils.TestCase

@HiltAndroidTest
class NavControllerExtTest : TestCase() {

    @Before
    fun setUp() {
        hiltRule.inject()
        composeTestRule.setContent {
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(
                navController = navController,
                startDestination = LoginRoutes.Root.getRoute()
            ) {
                loginGraph(navController)
            }
        }
    }

    @Test
    fun hasPreviousBackStackFalseInitially() {
        assertFalse(navController.hasPreviousBackStack())
    }

    @Test
    fun hasPreviousBackStackTrueForAddedBackStack() {
        composeTestRule.runOnUiThread {
            navController.setCurrentDestination(LoginRoutes.BioOptIn.getRoute())
        }
        assertTrue(navController.hasPreviousBackStack())
    }

    @Test
    fun closeAppPopsAllBackStack() {
        composeTestRule.runOnUiThread {
            // Fill up backstack
            navController.setCurrentDestination(LoginRoutes.BioOptIn.getRoute())
            navController.setCurrentDestination(LoginRoutes.Loading.getRoute())
            navController.setCurrentDestination(LoginRoutes.SignInError.getRoute())
            navController.setCurrentDestination(LoginRoutes.AnalyticsOptIn.getRoute())
            navController.closeApp()
        }
        assertFalse(navController.hasPreviousBackStack())
    }
}
