package uk.gov.onelogin.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.android.authentication.LoginSession
import uk.gov.android.features.FeatureFlags
import javax.inject.Inject

class LoginRoutes @Inject constructor(
    private val loginSession: LoginSession,
    private val featureFlags: FeatureFlags
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
                        loginSession = loginSession,
                        featureFlags = featureFlags
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
