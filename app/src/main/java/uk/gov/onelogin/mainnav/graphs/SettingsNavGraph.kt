package uk.gov.onelogin.mainnav.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uk.gov.onelogin.core.navigation.data.SettingsRoutes
import uk.gov.onelogin.features.settings.ui.OsslScreen

object SettingsNavGraph {
    fun NavGraphBuilder.settingsGraph() {
        composable(SettingsRoutes.Ossl.getRoute()) {
            OsslScreen()
        }
    }
}
