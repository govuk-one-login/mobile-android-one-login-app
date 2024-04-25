package uk.gov.onelogin

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import javax.inject.Inject
import uk.gov.onelogin.login.LoginRoutes.loginFlowRoutes
import uk.gov.onelogin.mainnav.nav.MainNavRoutes.mainNavRoutes
import uk.gov.onelogin.ui.error.ErrorRoutes.genericErrorRoute

class AppRoutes @Inject constructor() : IAppRoutes {
    @Composable
    override fun routes(
        navController: NavHostController,
        startDestination: String
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            loginFlowRoutes(navController)
            mainNavRoutes()
            genericErrorRoute(navController)
        }
    }
}
