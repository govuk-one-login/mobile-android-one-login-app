package uk.gov.onelogin.features.developer.ui.localauth

import uk.gov.android.localauth.devicesecurity.DeviceBiometricsManager
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsStatus

class MockDeviceBiometricManager(
    private val isDeviceSecure: Boolean,
    private val credentialStatus: DeviceBiometricsStatus
) : DeviceBiometricsManager {
    override fun isDeviceSecure(): Boolean = isDeviceSecure

    override fun getCredentialStatus(): DeviceBiometricsStatus = credentialStatus
}
