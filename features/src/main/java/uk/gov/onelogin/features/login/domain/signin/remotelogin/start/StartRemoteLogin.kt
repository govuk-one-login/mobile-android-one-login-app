package uk.gov.onelogin.features.login.domain.signin.remotelogin.start

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

fun interface StartRemoteLogin {
    suspend fun login(
        launcher: ActivityResultLauncher<Intent>,
        onFailure: (Throwable) -> Unit,
    )
}
