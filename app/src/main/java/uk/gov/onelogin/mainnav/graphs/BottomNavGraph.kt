package uk.gov.onelogin.mainnav.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import uk.gov.android.onelogin.BuildConfig
import uk.gov.onelogin.features.home.ui.HomeScreen
import uk.gov.onelogin.features.settings.ui.SettingsScreen
import uk.gov.onelogin.features.wallet.ui.WalletScreen
import uk.gov.onelogin.mainnav.ui.BottomNavDestination

object BottomNavGraph {
    fun NavGraphBuilder.bottomGraph(setShowNavBarState: (Boolean) -> Unit) {
        composable(BottomNavDestination.Home.key) {
            HomeScreen()
        }
        composable(
            BottomNavDestination.Wallet.key,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = createUrl("wallet-test")
                },
                navDeepLink {
                    uriPattern = createUrl("wallet")
                }
            )
        ) {
            WalletScreen(setShowNavBarState)
        }
        composable(BottomNavDestination.Settings.key) {
            SettingsScreen()
        }
    }

    private fun createUrl(pathPrefix: String): String {
        return if (BuildConfig.FLAVOR == "production") {
            "https://mobile.account.gov.uk/$pathPrefix"
        } else {
            "https://mobile.${BuildConfig.FLAVOR}.account.gov.uk/$pathPrefix"
        }
    }
}
