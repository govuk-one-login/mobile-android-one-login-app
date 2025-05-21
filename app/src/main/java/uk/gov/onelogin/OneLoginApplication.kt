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
    var appEntryPointProvider: () -> ApplicationEntryPoint = {
        EntryPointAccessors.fromApplication(
            this,
            ApplicationEntryPoint::class.java
        )
    }

    private val appEntryPoint: ApplicationEntryPoint by lazy { appEntryPointProvider() }

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        if (isLocalAuthEnabled() &&
            appEntryPoint.tokenRepository().getTokenResponse() != null
        ) {
            appEntryPoint.tokenRepository().clearTokenResponse()
            appEntryPoint.navigator().navigate(LoginRoutes.Start)
        }
        super.onStop(owner)
    }

    private fun isLocalAuthEnabled(): Boolean {
        val prefs = appEntryPoint.localAuthManager().localAuthPreference
        return !(prefs == LocalAuthPreference.Disabled || prefs == null)
    }
}
