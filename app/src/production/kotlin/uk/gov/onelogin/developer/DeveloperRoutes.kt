package uk.gov.onelogin.developer

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

object DeveloperRoutes {
    const val ROOT: String = "/developer"
    const val START: String = "$ROOT/start"

    fun NavGraphBuilder.developerFlowRoutes(navController: NavHostController) {
        // empty method to do nothing in Release
    }

    fun NavHostController.navigateToDeveloperPanel() {
        // empty method to do nothing in Release
    }
}
