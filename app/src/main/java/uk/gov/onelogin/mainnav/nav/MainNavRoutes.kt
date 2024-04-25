package uk.gov.onelogin.mainnav.nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.mainnav.ui.MainNavScreen

object MainNavRoutes {
    const val ROOT: String = "/home"
    const val START: String = "$ROOT/start"

    fun NavGraphBuilder.mainNavRoutesFlow() {
        navigation(
            route = ROOT,
            startDestination = START
        ) {
            composable(
                route = START
            ) {
                MainNavScreen()
            }
        }
    }
}
