package uk.gov.onelogin.mainnav.nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.developer.DeveloperRoutes.navigateToDeveloperPanel
import uk.gov.onelogin.mainnav.MainNavRoutes
import uk.gov.onelogin.mainnav.ui.MainNavScreen
import uk.gov.onelogin.signOut.SignOutRoutes

object MainNavGraph {
    fun NavGraphBuilder.mainNavRoutesFlow(navController: NavHostController) {
        navigation(
            route = MainNavRoutes.Root.getRoute(),
            startDestination = MainNavRoutes.Start.getRoute()
        ) {
            composable(
                route = MainNavRoutes.Start.getRoute()
            ) {
                MainNavScreen(
                    openSignOutScreen = {
                        navController.navigate(SignOutRoutes.ROOT)
                    },
                    openDeveloperPanel = {
                        navController.navigateToDeveloperPanel()
                    }
                )
            }
        }
    }
}
