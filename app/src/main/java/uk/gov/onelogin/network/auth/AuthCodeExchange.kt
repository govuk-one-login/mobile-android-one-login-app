package uk.gov.onelogin.network.auth

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.contentType
import uk.gov.onelogin.R
import uk.gov.onelogin.network.auth.response.TokenResponse
import uk.gov.onelogin.network.http.IHttpClient
import uk.gov.onelogin.network.utils.IOnlineChecker
import javax.inject.Inject

class AuthCodeExchange @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val httpClient: IHttpClient,
    private val onlineChecker: IOnlineChecker
): IAuthCodeExchange {
    @VisibleForTesting(otherwise = PRIVATE)
    private var redirectUrl: Url = Url(
        context.resources.getString(
            R.string.webBaseUrl,
            context.resources.getString(R.string.webRedirectEndpoint)
        )
    )

    @VisibleForTesting(otherwise = PRIVATE)
    private var url: Url = Url(
        context.resources.getString(
            R.string.apiBaseUrl,
            context.resources.getString(R.string.tokenExchangeEndpoint)
        )
    )

    override suspend fun exchangeCode(code: String): TokenResponse {
        if (code.isEmpty()) {
            throw AuthCodeExchangeCodeArgError("Code should be a non-empty string")
        }

        if (!onlineChecker.isOnline()) {
            throw AuthCodeExchangeOfflineError("The device appears to be offline")
        }

        val response = httpClient.client().post(url) {
            url {
                parameters.apply {
                    append("grant_type", "authorization_code" )
                    append("code", code)
                    append("redirect_uri", redirectUrl.toString())
                }
            }
            contentType(
                ContentType.Application.FormUrlEncoded
            )
        }

        return when {
            response.status >= HttpStatusCode.InternalServerError ->
                throw AuthCodeExchangeServerError(
                    "Server Error received - ${response.status} - ${response.bodyAsText()}"
                )
            response.status >= HttpStatusCode.BadRequest ->
                throw AuthCodeExchangeClientError(
                    "Client Error received - ${response.status} - ${response.bodyAsText()}"
                )
            response.status != HttpStatusCode.OK ->
                throw AuthCodeExchangeUnexpectedResponse("Unexpected response received - ${
                    response.status
                }")
            else -> Gson().fromJson(
                response.bodyAsText(),
                TokenResponse::class.java
            )
        }
    }

    companion object {
        class AuthCodeExchangeClientError constructor(message: String) : Error(message)
        class AuthCodeExchangeCodeArgError constructor(message: String) : Error(message)
        class AuthCodeExchangeOfflineError constructor(message: String) : Error(message)
        class AuthCodeExchangeServerError constructor(message: String) : Error(message)
        class AuthCodeExchangeUnexpectedResponse constructor(message: String) : Error(message)
    }
}
