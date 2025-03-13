package uk.gov.onelogin.mainnav.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uk.gov.onelogin.features.home.ui.HomeScreen
import uk.gov.onelogin.features.settings.ui.SettingsScreen
import uk.gov.onelogin.features.wallet.ui.WalletScreen
import uk.gov.onelogin.mainnav.ui.BottomNavDestination

object BottomNavGraph {
    fun NavGraphBuilder.bottomGraph() {
        composable(BottomNavDestination.Home.key) {
            HomeScreen()
        }
        composable(BottomNavDestination.Wallet.key) {
            WalletScreen()
        }
        composable(BottomNavDestination.Settings.key) {
            SettingsScreen()
        }
    }
}
