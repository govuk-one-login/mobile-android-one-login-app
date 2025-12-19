package uk.gov.onelogin.core.tokens.data

import javax.inject.Inject
import uk.gov.android.authentication.login.TokenResponse

class TokenRepositoryImpl @Inject constructor() : TokenRepository {
    private var tokenResponse: TokenResponse? = null

    override fun setTokenResponse(tokens: TokenResponse) {
        tokenResponse = TokenResponse(
            idToken = tokens.idToken,
            tokenType = tokens.tokenType,
            accessToken = tokens.accessToken,
            accessTokenExpirationTime = tokens.accessTokenExpirationTime,
            refreshToken = null
        )
    }

    override fun getTokenResponse(): TokenResponse? {
        return tokenResponse
    }

    override fun clearTokenResponse() {
        tokenResponse = null
    }

    override fun isTokenResponseClear(): Boolean {
        return tokenResponse == null
    }
}
