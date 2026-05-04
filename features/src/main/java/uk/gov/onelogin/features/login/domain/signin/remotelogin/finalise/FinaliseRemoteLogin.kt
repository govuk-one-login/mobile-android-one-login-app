package uk.gov.onelogin.features.login.domain.signin.remotelogin.finalise

import android.content.Intent
import uk.gov.android.authentication.login.TokenResponse

/**
 * Use case allowing to perform login (first time user) and
 */
fun interface FinaliseRemoteLogin {
    suspend fun handle(
        intent: Intent,
        onFailure: (Throwable?) -> Unit,
        onSuccess: (TokenResponse) -> Unit,
    )
}
