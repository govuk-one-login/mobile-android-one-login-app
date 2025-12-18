package uk.gov.onelogin.core.tokens.data

import uk.gov.android.authentication.login.TokenResponse

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

    /**
     * Clear the value of the [TokenResponse] currently held in the repository
     *
     */
    fun clearTokenResponse()

    /**
     * Check if the login tokens been persisted
     */
    fun areTokensPersisted(): Boolean

    /**
     * Set a value to determine whether the login tokens have been saved to memory (token repository)
     *
     * @param state token persisted state
     */
    fun setTokensPersistedState(state: Boolean)
}
