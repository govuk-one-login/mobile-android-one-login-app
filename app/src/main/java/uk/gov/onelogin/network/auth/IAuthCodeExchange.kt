package uk.gov.onelogin.network.auth

import uk.gov.onelogin.network.auth.response.TokenResponse

interface IAuthCodeExchange {
    suspend fun exchangeCode(code: String): TokenResponse
}
