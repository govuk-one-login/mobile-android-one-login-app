package uk.gov.onelogin.ui.error

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

object ErrorRoutes {
    const val ROOT: String = "/error"
    const val GENERIC: String = "$ROOT/generic"
    const val OFFLINE: String = "$ROOT/offline"
    const val OFFLINE_ERROR_TRY_AGAIN_KEY: String = "OFFLINE_ERROR_TRY_AGAIN_KEY"

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
            composable(
                route = OFFLINE
            ) {
                OfflineErrorScreen {
                    navController.apply {
                        previousBackStackEntry?.savedStateHandle?.set(
                            OFFLINE_ERROR_TRY_AGAIN_KEY,
                            true
                        )
                        popBackStack()
                    }
                }
            }
        }
    }

    fun NavHostController.navigateSingleTopTo(route: String) =
        this.navigate(route) { launchSingleTop = true }

    fun NavHostController.navigateToGenericErrorScreen() = this.navigateSingleTopTo(ROOT)
    fun NavHostController.navigateToOfflineErrorScreen() = this.navigateSingleTopTo(OFFLINE)
}
