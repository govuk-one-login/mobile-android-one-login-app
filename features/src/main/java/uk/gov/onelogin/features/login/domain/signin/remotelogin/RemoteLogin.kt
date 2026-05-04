package uk.gov.onelogin.features.login.domain.signin.remotelogin

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity

interface RemoteLogin {
    suspend fun start(launcher: ActivityResultLauncher<Intent>)

    suspend fun finalise(
        intent: Intent,
        isReAuth: Boolean = false,
        activity: FragmentActivity,
    )
}
