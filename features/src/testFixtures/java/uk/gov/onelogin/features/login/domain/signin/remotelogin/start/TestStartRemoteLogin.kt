package uk.gov.onelogin.features.login.domain.signin.remotelogin.start

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

class TestStartRemoteLogin: StartRemoteLogin {
    var result: Result<Unit> = Result.success(Unit)
    override suspend fun login(
        launcher: ActivityResultLauncher<Intent>,
        onFailure: (Throwable) -> Unit
    ) {
        val exception = result.exceptionOrNull()

        if (exception != null) {
            onFailure(exception)
            return
        }
    }
}
