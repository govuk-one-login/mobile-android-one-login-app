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
    suspend fun handle(
        intent: Intent,
        onFailure: (Throwable?) -> Unit,
        onSuccess: (TokenResponse) -> Unit,
    )
}
