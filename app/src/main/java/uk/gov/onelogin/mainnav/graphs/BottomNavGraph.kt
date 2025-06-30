package uk.gov.onelogin.mainnav.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import uk.gov.onelogin.features.home.ui.HomeScreen
import uk.gov.onelogin.features.settings.ui.SettingsScreen
import uk.gov.onelogin.features.wallet.ui.WalletScreen
import uk.gov.onelogin.mainnav.ui.BottomNavDestination

object BottomNavGraph {
    fun NavGraphBuilder.bottomGraph() {
        composable(BottomNavDestination.Home.key) {
            HomeScreen()
        }
        composable(
            BottomNavDestination.Wallet.key,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern =
                        "https://mobile.staging/wallet-test/add"
                },
                navDeepLink {
                    uriPattern =
                        "https://mobile.staging/wallet/add"
                }
            )
        ) {
            WalletScreen()
        }
        composable(BottomNavDestination.Settings.key) {
            SettingsScreen()
        }
    }
}
