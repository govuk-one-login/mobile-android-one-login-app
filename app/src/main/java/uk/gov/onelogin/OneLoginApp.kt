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
import uk.gov.onelogin.signOut.SignOutGraphObject.signOutGraph
import uk.gov.onelogin.ui.error.ErrorGraphObject.errorGraph

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
            mainNavRoutesFlow()
            errorGraph(navController)
            signOutGraph()
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
