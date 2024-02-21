package uk.gov.onelogin.credentialchecker

import android.content.Context
import androidx.biometric.BiometricManager as AndroidBiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BiometricManagerImpl @Inject constructor(
    @ApplicationContext
    context: Context
) : BiometricManager {
    private val androidBiometricManager = AndroidBiometricManager.from(context)
    override fun canAuthenticate(): BiometricStatus {
        return when (androidBiometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            AndroidBiometricManager.BIOMETRIC_SUCCESS ->
                BiometricStatus.SUCCESS

            AndroidBiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                BiometricStatus.NO_HARDWARE

            AndroidBiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                BiometricStatus.HARDWARE_UNAVAILABLE

            AndroidBiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricStatus.NOT_ENROLLED

            else ->
                BiometricStatus.UNKNOWN
        }
    }
}
