package uk.gov.onelogin.developer

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.R
import uk.gov.onelogin.ui.components.SimpleTextPage
import uk.gov.onelogin.ui.error.ErrorRoutes.navigateSingleTopTo

object DeveloperRoutes {
    const val ROOT: String = "/developer"
    const val START: String = "$ROOT/start"

    fun NavGraphBuilder.developerRoutes() {
        if (DeveloperTools.isDeveloperPanelEnabled())
            navigation(
                route = ROOT,
                startDestination = START
            ) {
                composable(
                    route = START
                ) {
                    SimpleTextPage(text = R.string.action_settings)
                }
            }
    }

    fun NavHostController.navigateToDeveloperPanel() {
        if (DeveloperTools.isDeveloperPanelEnabled()) this.navigateSingleTopTo(ROOT)
    }
}
