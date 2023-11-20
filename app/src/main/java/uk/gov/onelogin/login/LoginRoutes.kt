package uk.gov.onelogin.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.onelogin.R
import java.util.UUID

object LoginRoutes {
    const val ROOT: String = "/login"

    const val START: String = "$ROOT/start"
    const val LOADING: String = "$ROOT/loading"

    fun NavGraphBuilder.loginFlowRoutes(state: String) {
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
                val nonce = UUID.randomUUID().toString()
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
                Loading()
            }
        }
    }
}

@Composable
@Preview
fun Loading() {
    GdsTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
