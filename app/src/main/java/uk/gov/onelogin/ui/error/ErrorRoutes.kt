package uk.gov.onelogin.ui.error

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

object ErrorRoutes {
    const val ROOT: String = "/error"
    const val GENERIC: String = "$ROOT/generic"

    fun NavGraphBuilder.genericRoute() {
        navigation(
            route = ROOT,
            startDestination = GENERIC
        ) {
            composable(
                route = GENERIC
            ) {
                GenericErrorScreen()
            }
        }
    }
}
