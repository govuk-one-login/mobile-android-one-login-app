package uk.gov.onelogin

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.developer.DeveloperRoutes.developerFlowRoutes
import uk.gov.onelogin.mainnav.graphs.SettingsNavGraph.settingsGraph
import uk.gov.onelogin.navigation.graphs.ErrorGraphObject.errorGraph
import uk.gov.onelogin.navigation.graphs.LoginGraphObject.loginGraph
import uk.gov.onelogin.navigation.graphs.MainNavGraph.mainNavRoutesFlow
import uk.gov.onelogin.navigation.graphs.SignOutGraphObject.signOutGraph

@Composable
fun OneLoginApp(navController: NavHostController = rememberNavController()) {
    GdsTheme {
        NavHost(
            navController = navController,
            startDestination = LoginRoutes.Root.getRoute(),
        ) {
            loginGraph(navController)
            mainNavRoutesFlow()
            errorGraph(navController)
            signOutGraph(navController)
            developerFlowRoutes(navController)
            settingsGraph(navController)
        }
    }
}
