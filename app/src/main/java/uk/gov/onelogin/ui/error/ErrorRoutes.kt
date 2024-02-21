package uk.gov.onelogin.ui.error

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

object ErrorRoutes {
    const val ROOT: String = "/error"
    const val START: String = "$ROOT/start"

    fun NavGraphBuilder.genericErrorRoute(navController: NavHostController) {
        navigation(
            startDestination = START,
            route = ROOT
        ) {
            composable(
                route = START
            ) {
                GenericErrorScreen { navController.popBackStack() }
            }
        }
    }
    fun NavController.navigateSingleTopTo(route: String) =
        this.navigate(route) { launchSingleTop = true }
    fun NavController.navigateToGenericErrorScreen() = this.navigateSingleTopTo(ErrorRoutes.ROOT)
}
