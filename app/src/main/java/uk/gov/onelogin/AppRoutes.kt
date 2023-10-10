package uk.gov.onelogin

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import uk.gov.onelogin.login.LoginRoutes.loginFlowRoutes
import java.util.UUID
import uk.gov.onelogin.home.HomeRoutes.homeFlowRoutes
import uk.gov.onelogin.login.LoginRoutes

@Composable
fun AppRoutes(
    navController: NavHostController,
    startDestination: String = LoginRoutes.ROOT,
) {
    val state = UUID.randomUUID().toString()

    NavHost(navController = navController, startDestination = startDestination) {
        loginFlowRoutes(state)
        homeFlowRoutes()
    }
}
