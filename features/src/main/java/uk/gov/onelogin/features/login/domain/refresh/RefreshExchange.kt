package uk.gov.onelogin.features.login.domain.refresh

import androidx.fragment.app.FragmentActivity
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus

/**
 * Allows for the refresh token exchange to allow refreshing all tokens and extend the login sessions.
 */
interface RefreshExchange {
    /**
     * This does the required validity checks, the tokens network request and handles
     *
     * @param context - required for enabling retrieving the existing refresh token from the secure store
     * @param handleResult - lambda that should be called when exiting the function and includes the LocalAuthStatus to be handled accordingly by the consumer
     */
    suspend fun getTokens(
        context: FragmentActivity,
        handleResult: (LocalAuthStatus) -> Unit
    )

    /**
     * This allows to trigger the get tokens call starting from the client attestation checks.
     * It allows for retrying the flow directly (if required), assuming there is an existent persistent session ID and a valid refresh token.
     *
     * @param context - required for enabling retrieving the existing refresh token from the secure store
     * @param handleResult - lambda that should be called when exiting the function and includes the LocalAuthStatus to be handled accordingly by the consumer
     */
    suspend fun getClientAttestationAndRetrieveTokensFromSecureStore(
        context: FragmentActivity,
        handleResult: (LocalAuthStatus) -> Unit
    )
}
