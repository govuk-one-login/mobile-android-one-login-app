package uk.gov.android.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import java.util.UUID

class LoginSession : ILoginSession {
    private var context: Context? = null

    override fun init(
        context: Context
    ): ILoginSession {
        if (this.context == null) {
            this.context = context
        }
        return this
    }

    override fun present(
        configuration: LoginSessionConfiguration
    ) {
        if (context == null) {
            throw Error("Context is null, did you call init?")
        }

        with(configuration) {
            val context = this@LoginSession.context!!
            val nonce = UUID.randomUUID().toString()

            val serviceConfig = AuthorizationServiceConfiguration(
                authorizeEndpoint,
                tokenEndpoint
            )

            val builder = AuthorizationRequest.Builder(
                serviceConfig,
                clientId,
                responseType,
                redirectUri
            ).also {
                it.apply {
                    setScopes(scopes)
                    setUiLocales(locale)
                    setNonce(nonce)
                    setAdditionalParameters(
                        mapOf(
                            "vtr" to vectorsOfTrust
                        )
                    )
                }
            }

            val authRequest = builder.build()
            println("CSG - authRequest - ${authRequest.toUri()}")

            val authService = AuthorizationService(context)
            val authIntent = authService.getAuthorizationRequestIntent(authRequest)
            ActivityCompat.startActivityForResult(
                context as Activity,
                authIntent,
                REQUEST_CODE_AUTH,
                null
            )
        }
    }

    override fun finalise(intent: Intent) {
        println("CSG - ${intent.hasExtra("net.openid.appauth.AuthorizationResponse")}")
//        val response = AuthorizationResponse.fromIntent(intent)
//
//        if (response == null) {
//            val exception = AuthorizationException.fromIntent(intent)
//
//            println("CSG - exception = ${exception?.message}")
//            throw Exception(exception?.message)
//        }
//
//        println("CSG - accessToken = ${response.accessToken}")
    }

    companion object {
        const val REQUEST_CODE_AUTH = 418
    }
}
