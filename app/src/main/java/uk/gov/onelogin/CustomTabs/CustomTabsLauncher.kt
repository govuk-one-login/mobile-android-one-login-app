package uk.gov.onelogin.CustomTabs

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import javax.inject.Inject

class CustomTabsIntentLauncher @Inject constructor(
    private val intentBuilder: CustomTabsIntent.Builder
) : CustomTabsLauncher {

    private fun launch(
        context: Context,
        uri: Uri
    ) {
        val intent = intentBuilder.build()
        intent.launchUrl(context, uri)
    }

    override fun launch(
        context: Context,
        uriString: String
    ) = launch(context, Uri.parse(uriString))
}

fun interface CustomTabsLauncher {
    fun launch(
        context: Context,
        uriString: String
    )
}