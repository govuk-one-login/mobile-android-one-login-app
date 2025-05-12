package uk.gov.onelogin.core.tokens.data

import uk.gov.android.authentication.login.TokenResponse
import javax.inject.Inject

class TokenRepositoryImpl
    @Inject
    constructor() : TokenRepository {
        private var tokenResponse: TokenResponse? = null

        override fun setTokenResponse(tokens: TokenResponse) {
            tokenResponse = tokens
        }

        override fun getTokenResponse(): TokenResponse? = tokenResponse

        override fun clearTokenResponse() {
            tokenResponse = null
        }
    }
