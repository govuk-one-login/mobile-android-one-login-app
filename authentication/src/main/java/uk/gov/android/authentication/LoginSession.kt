package uk.gov.android.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.TokenResponse
import java.util.UUID

class LoginSession : ILoginSession {
    private var context: Context? = null
    private lateinit var authService: AuthorizationService

    override fun init(
        context: Context
    ): ILoginSession {
        if (this.context == null) {
            this.context = context
            authService = AuthorizationService(context)
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

            val authIntent = authService.getAuthorizationRequestIntent(authRequest)
            ActivityCompat.startActivityForResult(
                context as Activity,
                authIntent,
                REQUEST_CODE_AUTH,
                null
            )
        }
    }

    override fun finalise(intent: Intent, callback: (tokens: TokenResponse) -> Unit)  {
        val authorizationResponse = AuthorizationResponse.fromIntent(intent)

        if (authorizationResponse == null) {
            val exception = AuthorizationException.fromIntent(intent)

            throw Exception(exception?.message)
        }

        val exchangeRequest = authorizationResponse.createTokenExchangeRequest()

        authService.performTokenRequest(
            exchangeRequest
        ) { response, exception ->
            if (response == null) {
                throw Error(exception?.message)
            }

            callback(response)
        }
    }

    companion object {
        const val REQUEST_CODE_AUTH = 418
    }
}
