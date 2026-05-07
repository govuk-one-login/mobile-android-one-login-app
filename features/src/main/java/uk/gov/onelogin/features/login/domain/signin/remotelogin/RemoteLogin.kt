package uk.gov.onelogin.features.login.domain.signin.remotelogin

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity

/**
 * Use case that implements the login using the AppAuth library.
 * This is only called when a refresh exchange is not possible OR for a first time user.
 */
interface RemoteLogin {
    suspend fun start(launcher: ActivityResultLauncher<Intent>)

    suspend fun finalise(
        intent: Intent,
        isReAuth: Boolean = false,
        activity: FragmentActivity,
    )
}
