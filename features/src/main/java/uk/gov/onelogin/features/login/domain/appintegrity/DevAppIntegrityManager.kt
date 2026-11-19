package uk.gov.onelogin.features.login.domain.appintegrity

import uk.gov.android.authentication.integrity.AppIntegrityManager
import uk.gov.android.authentication.integrity.appcheck.model.AttestationResponse
import uk.gov.onelogin.features.developer.ui.appintegrity.AppIntegrityDevOptionsRepository
import uk.gov.onelogin.features.developer.ui.logging.DeveloperException

/**
 * Implementation of [AppIntegrityManager] that can be configured from the dev menu.
 */
class DevAppIntegrityManager(
    private val devOptionsRepository: AppIntegrityDevOptionsRepository,
    private val appCheck: AppIntegrityManager,
) : AppIntegrityManager by appCheck {
    override suspend fun getAttestation(): AttestationResponse {
        val forceError = devOptionsRepository.forceError
        if (forceError != null) {
            return AttestationResponse.Failure(
                reason = "Forced app integrity error",
                error = AppIntegrityException.ClientAttestationException(
                    e = DeveloperException("Forced app integrity error"),
                    type = forceError,
                ),
            )
        }

        return appCheck.getAttestation()
    }
}
