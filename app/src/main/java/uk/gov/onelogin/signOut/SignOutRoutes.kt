package uk.gov.onelogin.signOut

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.signOut.ui.SignOutScreen

object SignOutRoutes {
    const val ROOT: String = "/signOut"
    const val START: String = "$ROOT/start"

    fun NavGraphBuilder.signOutRoute(navController: NavHostController) {
        navigation(
            route = ROOT,
            startDestination = START
        ) {
            composable(
                route = START
            ) {
                SignOutScreen(
                    goBack = {
                        navController.popBackStack()
                    },
                    goToSignIn = {
                        navController.navigate(LoginRoutes.ROOT)
                    }
                )
            }
        }
    }
}
