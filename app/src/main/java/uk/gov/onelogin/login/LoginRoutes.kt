package uk.gov.onelogin.login

import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.onelogin.R
import uk.gov.onelogin.login.nonce.INonceGenerator
import javax.inject.Inject

class LoginRoutes @Inject constructor(
    private val nonceGenerator: INonceGenerator
): ILoginRoutes {
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
                    val baseUri = stringResource(
                        id = R.string.openIdConnectBaseUrl,
                        stringResource(id = R.string.openIdConnectAuthorizeEndpoint)
                    )
                    val redirectUri = stringResource(
                        id = R.string.webBaseUrl,
                        stringResource(id = R.string.webRedirectEndpoint)
                    )
                    val nonce = nonceGenerator.generate()
                    val clientID = stringResource(id = R.string.openIdConnectClientId)

                    UriBuilder(
                        state = state,
                        nonce = nonce,
                        baseUri = baseUri,
                        redirectUri = redirectUri,
                        clientID = clientID
                    ).apply {
                        WelcomeScreen(
                            builder = this
                        )
                    }
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
