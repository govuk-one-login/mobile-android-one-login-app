package uk.gov.onelogin.network.utils

import io.ktor.client.HttpClient
import io.ktor.http.Url
import uk.gov.onelogin.network.auth.IAuthCodeExchange
import uk.gov.onelogin.network.auth.response.TokenResponse

class AuthCodeExchangeStub(
    private val code: String,
    private val httpClient: HttpClient,
    onlineChecker: IOnlineChecker,
    private val redirectUrl: Url,
    private val url: Url
) : IAuthCodeExchange {
    override suspend fun exchangeCode(code: String): TokenResponse {
        return TokenResponse(
            access = "accessToken",
            expires = 180,
            id = "idToken",
            refresh = "refreshToken",
            scope = "scope",
            type = "type"
        )
    }
}
