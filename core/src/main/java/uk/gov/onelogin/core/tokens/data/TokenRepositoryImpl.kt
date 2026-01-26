package uk.gov.onelogin.core.tokens.data

import uk.gov.android.authentication.login.TokenResponse
import javax.inject.Inject

class TokenRepositoryImpl
    @Inject
    constructor() : TokenRepository {
        private var tokenResponse: TokenResponse? = null

        override fun setTokenResponse(tokens: TokenResponse) {
            tokenResponse =
                TokenResponse(
                    idToken = tokens.idToken,
                    tokenType = tokens.tokenType,
                    accessToken = tokens.accessToken,
                    accessTokenExpirationTime = tokens.accessTokenExpirationTime,
                    refreshToken = null,
                )
        }

        override fun getTokenResponse(): TokenResponse? = tokenResponse

        override fun clearTokenResponse() {
            tokenResponse = null
        }

        override fun isTokenResponseClear(): Boolean = tokenResponse == null
    }
