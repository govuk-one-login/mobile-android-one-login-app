package uk.gov.onelogin.credentialchecker

import android.app.KeyguardManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DeviceCredentialChecker @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val biometricManager: BiometricManager
) : CredentialChecker {
    override fun isDeviceSecure(): Boolean {
        val kgm = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return kgm.isDeviceSecure
    }

    override fun biometricStatus(): BiometricStatus {
        return biometricManager.canAuthenticate()
    }
}
