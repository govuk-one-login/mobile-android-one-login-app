package uk.gov.onelogin

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import uk.gov.onelogin.home.HomeRoutes.homeFlowRoutes
import uk.gov.onelogin.login.ILoginRoutes
import uk.gov.onelogin.login.state.IStateGenerator
import javax.inject.Inject

class AppRoutes @Inject constructor(
    private val loginRoutes: ILoginRoutes,
    private val stateGenerator: IStateGenerator
): IAppRoutes {
    @Composable
    override fun routes(
        navController: NavHostController,
        startDestination: String
    ) {
        val state = stateGenerator.generate()

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            loginRoutes.loginFlowRoutes(this, state)
            homeFlowRoutes()
        }
    }
}
