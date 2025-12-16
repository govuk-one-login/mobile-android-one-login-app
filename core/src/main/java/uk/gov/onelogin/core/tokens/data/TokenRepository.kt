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
     * Should the app should navigate to the re auth screen when the access token has expired?
     */
    fun shouldNavigateToReAuth(): Boolean

    /**
     * Set a value to determine whether the app should navigate to the re-auth screen when the
     * access token has expired
     *
     * @param navToReAuth Navigate to re-auth screen
     */
    fun setNavigateToReAuthState(navToReAuth: Boolean)
}
