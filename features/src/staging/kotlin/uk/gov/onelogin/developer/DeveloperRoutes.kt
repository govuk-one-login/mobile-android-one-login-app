package uk.gov.onelogin.developer

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.core.navigation.domain.navigateSingleTopTo
import uk.gov.onelogin.features.developer.ui.TabView

object DeveloperRoutes {
    const val ROOT: String = "/developer"
    const val START: String = "$ROOT/start"

    fun NavGraphBuilder.developerFlowRoutes(navController: NavHostController) {
        if (DeveloperTools.IS_DEVELOPER_PANEL_ENABLED) {
            navigation(
                route = ROOT,
                startDestination = START
            ) {
                composable(
                    route = START
                ) {
                    TabView {
                        navController.popBackStack()
                    }
                }
            }
        }
    }

    fun NavHostController.navigateToDeveloperPanel() {
        if (DeveloperTools.IS_DEVELOPER_PANEL_ENABLED) this.navigateSingleTopTo(ROOT)
    }
}
