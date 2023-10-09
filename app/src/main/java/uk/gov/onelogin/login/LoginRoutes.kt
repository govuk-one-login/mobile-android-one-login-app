package uk.gov.onelogin.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import java.util.UUID

object LoginRoutes {
    const val ROOT: String = "/login"

    const val START: String = "$ROOT/start"

    fun NavGraphBuilder.loginFlowRoutes(state: String) {
        navigation(
            route = ROOT,
            startDestination = START,
        ) {
            composable(
                route = START,
            ) {
                val baseUri = "https://oidc.staging.account.gov.uk/authorize"
                val redirectUri = "https://mobile-staging.account.gov.uk/redirect"
                val nonce = UUID.randomUUID().toString()
                val clientID = "CLIENT_ID"

                UriBuilder(
                    state = state,
                    nonce = nonce,
                    baseUri = baseUri,
                    redirectUri = redirectUri,
                    clientID = clientID,
                ).apply {
                    WelcomeScreen(
                        builder = this,
                    )
                }
            }
        }
    }
}
