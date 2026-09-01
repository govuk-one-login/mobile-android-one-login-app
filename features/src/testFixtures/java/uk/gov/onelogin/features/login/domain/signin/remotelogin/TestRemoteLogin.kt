package uk.gov.onelogin.features.login.domain.signin.remotelogin

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation

class TestRemoteLogin : RemoteLogin {
    var started = false
        private set
    var startCancelled = false
        private set
    var finalisedWith: FinalArgs? = null
        private set

    var startResult: RemoteLogin.Result = RemoteLogin.Result.Success
    var finaliseResult: RemoteLogin.Result = RemoteLogin.Result.Success

    /**
     * Setting this to true
     */
    var startWillComplete: Boolean = true

    override suspend fun start(launcher: ActivityResultLauncher<Intent>): RemoteLogin.Result {
        started = true
        try {
            if (!startWillComplete) {
                awaitCancellation()
            }
        } catch (e: CancellationException) {
            startCancelled = true
            throw e
        }
        return startResult
    }

    override suspend fun finalise(
        intent: Intent,
        isReAuth: Boolean,
        activity: FragmentActivity,
    ): RemoteLogin.Result {
        finalisedWith = FinalArgs(intent, isReAuth)
        return finaliseResult
    }

    data class FinalArgs(
        val intent: Intent,
        val isReAuth: Boolean,
    )
}
