package uk.gov.onelogin.login

import android.net.Uri
import java.util.UUID

data class UriBuilder(
    val state: String,
    val nonce: String = UUID.randomUUID().toString(),
) {
    fun build(
        baseUri: String,
        redirectUri: String,
        clientID: String
    ): Uri {
        return Uri.parse(baseUri)
            .buildUpon().appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope", "openid email phone offline_access")
            .appendQueryParameter("client_id", clientID)
            .appendQueryParameter("state", state)
            .appendQueryParameter(
                "redirect_uri", redirectUri,
            )
            .appendQueryParameter("nonce", nonce)
            .appendQueryParameter("vtr", "[\"Cl.Cm.P0\"]")
            .appendQueryParameter("ui_locales", "en")
            .build()
    }
}
