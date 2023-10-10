package uk.gov.onelogin.login

import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.R
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
                val baseUri = stringResource(
                    id = R.string.openIdConnectBaseUrl,
                    stringResource(id = R.string.openIdConnectAuthorizeEndpoint),
                )
                val redirectUri = stringResource(
                    id = R.string.webBaseUrl,
                    stringResource(id = R.string.webRedirectEndpoint),
                )
                val nonce = UUID.randomUUID().toString()
                val clientID = stringResource(id = R.string.openIdConnectClientId)

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
