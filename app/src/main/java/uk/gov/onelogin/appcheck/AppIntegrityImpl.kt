package uk.gov.onelogin.appcheck

import javax.inject.Inject
import uk.gov.android.authentication.integrity.ClientAttestationManager
import uk.gov.android.authentication.integrity.model.AttestationResponse
import uk.gov.android.features.FeatureFlags
import uk.gov.onelogin.features.AppCheckFeatureFlag

class AppIntegrityImpl @Inject constructor(
    private val featureFlags: FeatureFlags,
    private val appCheck: ClientAttestationManager
) : AppIntegrity {
    override suspend fun startCheck(): AppIntegrityResult {
        return if (featureFlags[AppCheckFeatureFlag.ENABLED]) {
            when (val result = appCheck.getAttestation()) {
                is AttestationResponse.Success -> AppIntegrityResult.Success(result.attestationJwt)
                is AttestationResponse.Failure -> AppIntegrityResult.Failure(result.reason)
            }
        } else {
            AppIntegrityResult.NotRequired
        }
    }
}
