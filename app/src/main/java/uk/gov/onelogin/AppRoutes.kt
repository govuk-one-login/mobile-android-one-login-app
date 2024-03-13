package uk.gov.onelogin

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import javax.inject.Inject
import uk.gov.onelogin.login.LoginRoutes.loginFlowRoutes
import uk.gov.onelogin.ui.error.ErrorRoutes.genericErrorRoute
import uk.gov.onelogin.ui.home.HomeRoutes.homeFlowRoutes

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
            homeFlowRoutes()
            genericErrorRoute(navController)
        }
    }
}
