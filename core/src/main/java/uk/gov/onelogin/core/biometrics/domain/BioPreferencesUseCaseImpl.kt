package uk.gov.onelogin.core.biometrics.domain

import javax.inject.Inject

class BioPreferencesUseCaseImpl @Inject constructor(
    private val biometricPreferenceHandler: BiometricPreferenceHandler,
    private val credentialChecker: CredentialChecker
) : BioPreferencesUseCase {
    override suspend fun reset() {
        if (!credentialChecker.isDeviceSecure()) {
            biometricPreferenceHandler.clean()
        }
    }
}
