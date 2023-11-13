package uk.gov.onelogin.network.auth

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.contentType
import uk.gov.onelogin.network.auth.response.TokenResponse
import uk.gov.onelogin.network.utils.IOnlineChecker

class AuthCodeExchange constructor(
    private val code: String,
    private val httpClient: HttpClient,
    onlineChecker: IOnlineChecker,
    private val redirectUrl: Url,
    private val url: Url
) {
    init {
        if (code.isEmpty()) {
            throw AuthCodeExchangeCodeArgError("Code should be a non-empty string")
        }

        if (!onlineChecker.isOnline()) {
            throw AuthCodeExchangeOfflineError("The device appears to be offline")
        }
    }
    suspend fun send(): TokenResponse {
        val response = httpClient.post(url) {
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
                throw AuthCodeExchangeServerError("Server Error received - ${response.status}")
            response.status >= HttpStatusCode.BadRequest ->
                throw AuthCodeExchangeClientError("Client Error received - ${response.status}")
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
