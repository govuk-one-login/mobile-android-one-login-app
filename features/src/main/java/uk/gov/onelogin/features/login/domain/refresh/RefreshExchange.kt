package uk.gov.onelogin.features.login.domain.refresh

import androidx.fragment.app.FragmentActivity
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus

/**
 * Allows for the refresh token exchange to allow refreshing all tokens and extend the login sessions.
 */
fun interface RefreshExchange {
    /**
     * This does the required validity checks, the tokens network request and handles
     *
     * @param context - required for enabling retrieving the existing refresh token from the secure store
     * @param handleResult - lambda that should be called when exiting the function and includes the LocalAuthStatus to be handled accordingly by the consumer
     */
    suspend fun getTokens(
        context: FragmentActivity,
        handleResult: (LocalAuthStatus) -> Unit,
    )
}
