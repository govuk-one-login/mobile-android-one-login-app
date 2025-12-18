package uk.gov.onelogin.core.tokens.data

import javax.inject.Inject
import uk.gov.android.authentication.login.TokenResponse

class TokenRepositoryImpl @Inject constructor() : TokenRepository {
    private var tokenResponse: TokenResponse? = null
    private var navigateToReAuth: Boolean = false

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

    override fun areTokensPersisted(): Boolean {
        return navigateToReAuth
    }

    override fun setTokensPersistedState(state: Boolean) {
        navigateToReAuth = state
    }
}
