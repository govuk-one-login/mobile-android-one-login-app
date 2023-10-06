package uk.gov.onelogin.login

import android.net.Uri
import java.util.UUID

data class WelcomeScreenUrl(
    val state: String,
    val nonce: String = UUID.randomUUID().toString(),
) {
    fun build(): Uri {
        return Uri.parse("https://oidc.staging.account.gov.uk/authorize")
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
    }
}
