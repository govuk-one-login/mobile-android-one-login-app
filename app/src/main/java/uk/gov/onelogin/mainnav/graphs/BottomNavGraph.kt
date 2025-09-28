package uk.gov.onelogin.mainnav.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import uk.gov.android.onelogin.BuildConfig
import uk.gov.onelogin.features.home.ui.HomeScreen
import uk.gov.onelogin.features.settings.ui.SettingsScreen
import uk.gov.onelogin.features.wallet.ui.WalletScreen
import uk.gov.onelogin.mainnav.ui.BottomNavDestination
import uk.gov.onelogin.mainnav.ui.DEEP_LINK_ARG

object BottomNavGraph {
    fun NavGraphBuilder.bottomGraph(setDisplayContentAsFullScreen: (Boolean) -> Unit) {
        composable(BottomNavDestination.Home.key) {
            HomeScreen()
        }
        composable(
            BottomNavDestination.Wallet.key + "/{$DEEP_LINK_ARG}",
            arguments = listOf(
                navArgument(DEEP_LINK_ARG) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = createUrl("wallet/{$DEEP_LINK_ARG}")
                }
            )
        ) { backStackEntry ->
            val argument = backStackEntry.arguments?.getBoolean(DEEP_LINK_ARG) ?: false
            WalletScreen(argument, setDisplayContentAsFullScreen)
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
