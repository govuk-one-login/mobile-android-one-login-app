package uk.gov.onelogin.mainnav.nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.developer.DeveloperRoutes.navigateToDeveloperPanel
import uk.gov.onelogin.mainnav.ui.MainNavScreen
import uk.gov.onelogin.signOut.SignOutRoutes

object MainNavRoutes {
    const val ROOT: String = "/home"
    const val START: String = "$ROOT/start"

    fun NavGraphBuilder.mainNavRoutesFlow(navController: NavHostController) {
        navigation(
            route = ROOT,
            startDestination = START
        ) {
            composable(
                route = START
            ) {
                MainNavScreen(
                    openSignOutScreen = {
                        navController.navigate(SignOutRoutes.ROOT)
                    },
                    openDeveloperPanel = {
                        navController.navigateToDeveloperPanel()
                    })
            }
        }
    }
}
