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
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        installSplashScreen().apply {}

        val signInButton = findViewById<Button>(R.id.signin,)
            .setOnClickListener {
                val url = "https://gov.uk"
                val intent = CustomTabsIntent.Builder()
                    .build()
                intent.launchUrl(this@MainActivity, Uri.parse(url))
            }

    }
}