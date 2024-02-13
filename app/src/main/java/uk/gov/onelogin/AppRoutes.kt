package uk.gov.onelogin

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import uk.gov.onelogin.login.LoginRoutes.loginFlowRoutes
import uk.gov.onelogin.ui.home.HomeRoutes.homeFlowRoutes
import javax.inject.Inject

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
        }
    }
}
