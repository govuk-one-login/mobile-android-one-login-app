package uk.gov.onelogin.features.login.domain.signin.remotelogin.finalise

import android.content.Intent
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.onelogin.features.login.domain.signin.remotelogin.start.StartRemoteLogin

/**
 * Gets access and refresh tokens by exchanging the response from the browser login redirect.
 *
 * The caller is responsible for saving the tokens.
 */
fun interface FinaliseRemoteLogin {
    /**
     * @param intent The intent received after starting a remote login with [StartRemoteLogin]
     */
    suspend fun handle(intent: Intent): Result

    sealed class Result {
        data class Success(val tokenResponse: TokenResponse) : Result()

        data class Failure(val error: Throwable) : Result()
    }
}
