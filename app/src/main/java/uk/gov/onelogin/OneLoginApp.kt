package uk.gov.onelogin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import uk.gov.android.ui.theme.GdsTheme
import uk.gov.onelogin.developer.DeveloperRoutes.developerFlowRoutes
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.LoginRoutes.loginFlowRoutes
import uk.gov.onelogin.mainnav.nav.MainNavRoutes.mainNavRoutesFlow
import uk.gov.onelogin.signOut.SignOutRoutes.signOutRoute
import uk.gov.onelogin.ui.error.ErrorRoutes.genericErrorRoute

@Composable
fun OneLoginApp(
    viewModel: MainActivityViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current as FragmentActivity

    GdsTheme {
        NavHost(
            navController = navController,
            startDestination = LoginRoutes.ROOT
        ) {
            loginFlowRoutes(navController)
            mainNavRoutesFlow(navController)
            genericErrorRoute(navController)
            signOutRoute(navController)
            developerFlowRoutes(navController)
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.next.observe(context) {
            navController.navigate(it) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
    }
}
