package uk.gov.onelogin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import uk.gov.android.ui.theme.GdsTheme
import uk.gov.onelogin.developer.DeveloperRoutes.developerFlowRoutes
import uk.gov.onelogin.login.LoginGraphObject.loginGraph
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.mainnav.nav.MainNavGraph.mainNavRoutesFlow
import uk.gov.onelogin.signOut.SignOutRoutes.signOutRoute
import uk.gov.onelogin.ui.error.ErrorRoutes.genericErrorRoute

@Composable
fun OneLoginApp(
    viewModel: MainActivityViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    GdsTheme {
        NavHost(
            navController = navController,
            startDestination = LoginRoutes.Root.getRoute()
        ) {
            loginGraph(navController)
            mainNavRoutesFlow(navController)
            genericErrorRoute(navController)
            signOutRoute(navController)
            developerFlowRoutes(navController)
        }
    }

    DisposableEffect(key1 = lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(viewModel)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(viewModel)
        }
    }
}
