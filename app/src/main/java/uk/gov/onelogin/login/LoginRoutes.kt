package uk.gov.onelogin.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.android.authentication.LoginSession
import javax.inject.Inject

class LoginRoutes @Inject constructor(
    private val loginSession: LoginSession
) : ILoginRoutes {
    override fun loginFlowRoutes(
        navGraphBuilder: NavGraphBuilder,
        state: String
    ) {
        navGraphBuilder.apply {
            navigation(
                route = ROOT,
                startDestination = LOADING
            ) {
                composable(
                    route = START
                ) {
                    WelcomeScreen(
                        loginSession = loginSession
                    )
                }

                composable(
                    route = LOADING
                ) {
                    LoadingScreen()
                }
            }
        }
    }

    companion object {
        const val ROOT: String = "/login"

        const val START: String = "$ROOT/start"
        const val LOADING: String = "$ROOT/loading"
    }
}
