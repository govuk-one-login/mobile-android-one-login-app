package uk.gov.onelogin

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import java.io.OutputStream
import java.io.PrintStream
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.android.onelogin.BuildConfig
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

        if (!BuildConfig.DEBUG) {
            System.setOut(
                PrintStream(object : OutputStream() {
                    override fun write(p0: Int) {
                        // do nothing
                    }
                })
            )
            System.setErr(
                PrintStream(object : OutputStream() {
                    override fun write(p0: Int) {
                        // do nothing
                    }
                })
            )
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        if (isLocalAuthEnabled() &&
            appEntryPoint.tokenRepository().getTokenResponse() != null &&
            !appEntryPoint.isIdCheckSessionActive().isIdCheckActive()
        ) {
            appEntryPoint.tokenRepository().clearTokenResponse()
            appEntryPoint.navigator().navigate(LoginRoutes.Start)
        }
        super.onStop(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        // If a debugger is detected in release, kill the app
        if (android.os.Debug.isDebuggerConnected() && !BuildConfig.DEBUG) {
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }

    private fun isLocalAuthEnabled(): Boolean {
        val prefs = appEntryPoint.localAuthManager().localAuthPreference
        return !(prefs == LocalAuthPreference.Disabled || prefs == null)
    }
}
