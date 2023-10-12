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

@Composable
fun SigninScreen(
    builder: UriBuilder,
    context: Context = LocalContext.current,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = {
            val intent = Builder()
                .build()
            intent.launchUrl(context, builder.url)
        }) {
            Text(text = "Sign In")
        }
    }
}
