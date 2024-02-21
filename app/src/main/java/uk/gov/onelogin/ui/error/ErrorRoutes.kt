package uk.gov.onelogin.ui.error

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

object ErrorRoutes {
    const val ROOT: String = "/error"
    const val GENERIC: String = "$ROOT/generic"

    fun NavGraphBuilder.genericErrorRoute(navController: NavHostController) {
        navigation(
            startDestination = GENERIC,
            route = ROOT
        ) {
            composable(
                route = GENERIC
            ) {
                GenericErrorScreen { navController.popBackStack() }
            }
        }
    }
    fun NavHostController.navigateSingleTopTo(route: String) =
        this.navigate(route) { launchSingleTop = true }
    fun NavHostController.navigateToGenericErrorScreen() = this.navigateSingleTopTo(ROOT)
}
