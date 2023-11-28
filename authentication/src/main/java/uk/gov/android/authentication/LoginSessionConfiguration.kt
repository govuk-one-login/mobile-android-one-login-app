package uk.gov.android.authentication

import android.net.Uri
import net.openid.appauth.ResponseTypeValues

data class LoginSessionConfiguration(
    val authorizeEndpoint: Uri,
    val clientId: String,
    val locale: String = "en",
    val prefersEphemeralWebSession: Boolean = true,
    val redirectUri: Uri,
    val responseType: String = ResponseTypeValues.CODE,
    val scopes: String,
    val tokenEndpoint: Uri,
    val vectorsOfTrust: String = "[\"Cl.Cm.P0\"]"
)
