package uk.gov.onelogin.features.login.domain.signin.remotelogin.start

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

class TestStartRemoteLogin : StartRemoteLogin {
    var result: StartRemoteLogin.Result = StartRemoteLogin.Result.Success

    override suspend fun login(
        launcher: ActivityResultLauncher<Intent>,
    ): StartRemoteLogin.Result = result
}
