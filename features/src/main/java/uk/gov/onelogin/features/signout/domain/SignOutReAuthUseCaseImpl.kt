package uk.gov.onelogin.features.signout.domain

import javax.inject.Inject
import uk.gov.onelogin.core.biometrics.domain.BiometricPreferenceHandler
import uk.gov.onelogin.core.biometrics.domain.CredentialChecker

class SignOutReAuthUseCaseImpl @Inject constructor(
    private val biometricPreferenceHandler: BiometricPreferenceHandler,
    private val credentialChecker: CredentialChecker
) : SignOutReAuthUseCase {
    override suspend fun resetBioPreferences() {
        if (!credentialChecker.isDeviceSecure()) {
            biometricPreferenceHandler.clean()
        }
    }
}
