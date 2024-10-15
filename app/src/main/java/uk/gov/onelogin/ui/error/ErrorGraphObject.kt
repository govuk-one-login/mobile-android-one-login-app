package uk.gov.onelogin.ui.error

import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.ui.error.update.UpdateRequiredScreen

object ErrorGraphObject {
    const val OFFLINE_ERROR_TRY_AGAIN_KEY: String = "OFFLINE_ERROR_TRY_AGAIN_KEY"

    fun NavGraphBuilder.errorGraph(navController: NavHostController) {
        navigation(
            startDestination = ErrorRoutes.Generic.getRoute(),
            route = ErrorRoutes.Root.getRoute()
        ) {
            composable(
                route = ErrorRoutes.Generic.getRoute()
            ) {
                GenericErrorScreen { navController.popBackStack() }
            }
            composable(
                route = ErrorRoutes.SignOut.getRoute()
            ) {
                val context = LocalContext.current as FragmentActivity
                SignOutErrorScreen {
                    context.finishAndRemoveTask()
                }
            }
            composable(
                route = ErrorRoutes.Offline.getRoute()
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
            composable(
                route = ErrorRoutes.UpdateRequired.getRoute()
            ) {
                UpdateRequiredScreen()
            }
        }
    }
}
