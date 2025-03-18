package uk.gov.onelogin.mainnav.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uk.gov.onelogin.core.navigation.data.SettingsRoutes
import uk.gov.onelogin.features.settings.ui.OsslScreen

object SettingsNavGraph {
    fun NavGraphBuilder.settingsGraph(
        navController: NavController
    ) {
        composable(SettingsRoutes.Ossl.getRoute()) {
            OsslScreen {
                navController.popBackStack()
            }
        }
    }
}
