package uk.gov.onelogin.login

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent.Builder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import uk.gov.onelogin.R
import java.util.UUID

@Composable
fun WelcomeScreen(
    state: String,
    nonce: String = UUID.randomUUID().toString(),
    context: Context = LocalContext.current
) {
    val baseUri = "https://oidc.staging.account.gov.uk/authorize"
    val redirectUri = "https://mobile-staging.account.gov.uk/redirect"
    val clientID = "CLIENT_ID"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = {
            val url = UriBuilder(
                state = state,
                nonce = nonce,
                baseUri = baseUri,
                redirectUri = redirectUri,
                clientID = clientID,
            )
            val intent = Builder()
                .build()
            intent.launchUrl(context, url.url)
        }) {
            Text(text = "Sign In")
        }
    }
}
