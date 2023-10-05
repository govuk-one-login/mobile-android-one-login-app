package uk.gov.onelogin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import java.util.UUID


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {}
        val state = UUID.randomUUID().toString()
        setContent { 
            GdsTheme {
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        val nonce = UUID.randomUUID().toString()
                        val url = Uri.parse("https://oidc.staging.account.gov.uk/authorize")
                            .buildUpon().appendQueryParameter("response_type", "code")
                            .appendQueryParameter("scope", "openid email phone offline_access")
                            .appendQueryParameter("client_id", "CLIENT_ID")
                            .appendQueryParameter("state", state)
                            .appendQueryParameter("redirect_uri", "https://mobile-staging.account.gov.uk/redirect")
                            .appendQueryParameter("nonce", nonce)
                            .appendQueryParameter("vtr", "[\"Cl.Cm.P0\"]")
                            .appendQueryParameter("ui_locales", "en")
                            .build()
                        val intent = CustomTabsIntent.Builder()
                            .build()
                        intent.launchUrl(this@MainActivity, url)
                    }) {
                        Text(text = "Sign In")
                    }
                }
            }

//            TODO extract url into a new function (base uri, redirect and client id will change)
//            extract sign in button composable
        }
    }
}