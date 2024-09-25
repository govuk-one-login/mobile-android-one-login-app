package uk.gov.onelogin.signOut

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.signOut.ui.SignOutScreen

object SignOutGraphObject {
    fun NavGraphBuilder.signOutGraph() {
        navigation(
            route = SignOutRoutes.Root.getRoute(),
            startDestination = SignOutRoutes.Start.getRoute()
        ) {
            composable(
                route = SignOutRoutes.Start.getRoute()
            ) {
                SignOutScreen()
            }
        }
    }
}
