package uk.gov.onelogin.network.auth

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.contentType
import uk.gov.onelogin.network.utils.IOnlineChecker

class TokenExchange constructor(
    private val code: String,
    private val httpClient: HttpClient,
    onlineChecker: IOnlineChecker,
    private val redirectUrl: Url,
    private val url: Url
) {
    init {
        if (code.isEmpty()) {
            throw TokenExchangeCodeArgError("Code should be a non-empty string")
        }

        if (!onlineChecker.isOnline()) {
            throw TokenExchangeOfflineError("The device appears to be offline")
        }
    }
    suspend fun send() {
        val response = httpClient.post {
            url(url.toString()) {
                parameters.append(
                    "grant_type",
                    "authorization_code"
                )
                parameters.append(
                    "code",
                    code
                )
                parameters.append(
                    "redirect_uri",
                    redirectUrl.toString()
                )
            }
            contentType(
                ContentType.Application.FormUrlEncoded
            )
        }

        if (response.status >= HttpStatusCode.InternalServerError) {
            throw TokenExchangeServerError("Server Error received - ${response.status}")
        }
    }

    companion object {
        class TokenExchangeCodeArgError constructor(message: String) : Error(message)
        class TokenExchangeOfflineError constructor(message: String) : Error(message)
        class TokenExchangeServerError constructor(message: String) : Error(message)
    }
}
