package uk.gov.onelogin

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.onelogin.core.ApplicationEntryPoint
import uk.gov.onelogin.core.navigation.data.LoginRoutes

@HiltAndroidApp
class OneLoginApplication : Application(), DefaultLifecycleObserver {
    private var appEntryPoint: ApplicationEntryPoint? = null

    override fun onCreate() {
        super<Application>.onCreate()
        appEntryPoint = EntryPointAccessors.fromApplication(
            this,
            ApplicationEntryPoint::class.java
        )
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        if (isLocalAuthEnabled() &&
            appEntryPoint?.tokenRepository()?.getTokenResponse() != null
        ) {
            appEntryPoint?.tokenRepository()?.clearTokenResponse()
            appEntryPoint?.navigator()?.navigate(LoginRoutes.Start)
        }
    }

    private fun isLocalAuthEnabled(): Boolean {
        val prefs = appEntryPoint?.localAuthManager()?.localAuthPreference
        return !(prefs == LocalAuthPreference.Disabled || prefs == null)
    }
}
