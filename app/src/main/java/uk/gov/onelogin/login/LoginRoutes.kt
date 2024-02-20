package uk.gov.onelogin.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.ui.home.HomeRoutes

object LoginRoutes {
    fun NavGraphBuilder.loginFlowRoutes(
        navController: NavController
    ) {
        navigation(
            route = ROOT,
            startDestination = START
        ) {
            composable(
                route = START
            ) {
                WelcomeScreen()
            }

            composable(
                route = LOADING
            ) {
                LoadingScreen()
            }

            composable(
                route = PASSCODE_INFO
            ) {
                PasscodeInfoScreen {
                    navController.navigate(HomeRoutes.START)
                }
            }
        }
    }

    const val ROOT: String = "/login"
    const val START: String = "$ROOT/start"
    const val LOADING: String = "$ROOT/loading"
    const val PASSCODE_INFO: String = "$ROOT/passcode_error"
}
