package uk.gov.onelogin.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

object HomeRoutes {
    const val ROOT: String = "/home"
    const val START: String = "$ROOT/start"

    fun NavGraphBuilder.homeFlowRoutes() {
        navigation(
            route = ROOT,
            startDestination = START
        ) {
            composable(
                route = START
            ) {
            }
        }
    }
}
