package uk.gov.onelogin

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import uk.gov.onelogin.login.ILoginRoutes
import uk.gov.onelogin.login.state.IStateGenerator
import uk.gov.onelogin.network.http.IHttpClient
import uk.gov.onelogin.ui.home.HomeRoutes.homeFlowRoutes
import javax.inject.Inject

class AppRoutes @Inject constructor(
    private val loginRoutes: ILoginRoutes,
    private val stateGenerator: IStateGenerator,
    private val httpClient: IHttpClient
) : IAppRoutes {
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
            homeFlowRoutes(httpClient)
        }
    }
}
