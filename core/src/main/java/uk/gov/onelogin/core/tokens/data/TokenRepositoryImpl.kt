package uk.gov.onelogin.core.tokens.data

import javax.inject.Inject
import uk.gov.onelogin.core.tokens.data.tokendata.LoginTokens

class TokenRepositoryImpl
@Inject
constructor() : TokenRepository {
    private var tokenResponse: LoginTokens? = null

    override fun setTokenResponse(tokens: LoginTokens) {
        tokenResponse =
            LoginTokens(
                idToken = tokens.idToken,
                tokenType = tokens.tokenType,
                accessToken = tokens.accessToken,
                accessTokenExpirationTime = tokens.accessTokenExpirationTime,
            )
    }

    override fun getTokenResponse(): LoginTokens? = tokenResponse

    override fun clearTokenResponse() {
        tokenResponse = null
    }

    override fun isTokenResponseClear(): Boolean = tokenResponse == null
}
