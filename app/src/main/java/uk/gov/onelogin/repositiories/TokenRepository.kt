package uk.gov.onelogin.repositiories

import javax.inject.Inject
import uk.gov.android.authentication.TokenResponse

/**
 * Repository to hold value of tokens in working memory. [TokenResponse] held in repository has initial null value
 */
interface TokenRepository {
    /**
     * Set a value for the [TokenResponse] currently held in the repository
     *
     * @param tokens The [TokenResponse] to set
     */
    fun setTokenResponse(tokens: TokenResponse)

    /**
     * Retrieve the currently held [TokenResponse] in the repository, may be null
     *
     * @return [TokenResponse]
     */
    fun getTokenResponse(): TokenResponse?
}

class TokenResponseImpl @Inject constructor() : TokenRepository {
    private var tokenResponse: TokenResponse? = null
    override fun setTokenResponse(tokens: TokenResponse) {
        tokenResponse = tokens
    }

    override fun getTokenResponse(): TokenResponse? {
        return tokenResponse
    }
}
