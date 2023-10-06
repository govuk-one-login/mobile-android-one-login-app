package uk.gov.onelogin.login

import android.content.Context
import android.net.Uri
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
import java.util.UUID

@Composable
fun WelcomeScreen(
    state: String,
    nonce: String = UUID.randomUUID().toString(),
    context: Context = LocalContext.current,
) {
    // DCMAW-6345: extract url into a new function (base uri, redirect and client id will change)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = {
            val url = Uri.parse("https://oidc.staging.account.gov.uk/authorize")
                .buildUpon().appendQueryParameter("response_type", "code")
                .appendQueryParameter("scope", "openid email phone offline_access")
                .appendQueryParameter("client_id", "CLIENT_ID")
                .appendQueryParameter("state", state)
                .appendQueryParameter(
                    "redirect_uri",
                    "https://mobile-staging.account.gov.uk/redirect",
                )
                .appendQueryParameter("nonce", nonce)
                .appendQueryParameter("vtr", "[\"Cl.Cm.P0\"]")
                .appendQueryParameter("ui_locales", "en")
                .build()
            val intent = Builder()
                .build()
            intent.launchUrl(context, url)
        }) {
            Text(text = "Sign In")
        }
    }
}
