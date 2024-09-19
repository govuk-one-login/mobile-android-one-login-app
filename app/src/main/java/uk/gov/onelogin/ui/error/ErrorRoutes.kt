package uk.gov.onelogin.ui.error

import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

object ErrorRoutes {
    const val ROOT: String = "/error"
    const val GENERIC: String = "$ROOT/generic"
    const val OFFLINE: String = "$ROOT/offline"
    const val OFFLINE_ERROR_TRY_AGAIN_KEY: String = "OFFLINE_ERROR_TRY_AGAIN_KEY"
    const val SIGN_OUT: String = "$ROOT/signOut"

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
                route = SIGN_OUT
            ) {
                val context = LocalContext.current as FragmentActivity
                SignOutErrorScreen {
                    context.finishAndRemoveTask()
                }
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
}
