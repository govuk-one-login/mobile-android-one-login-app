package uk.gov.onelogin.appcheck

import uk.gov.android.features.FeatureFlags
import uk.gov.onelogin.features.AppCheckFeatureFlag
import uk.gov.onelogin.integrity.ClientAttestationManager
import uk.gov.onelogin.integrity.model.AttestationResponse
import javax.inject.Inject

class AppIntegrityImpl @Inject constructor(
    private val featureFlags: FeatureFlags,
    private val appCheck: ClientAttestationManager
) : AppIntegrity {
    override suspend fun startCheck(): AppIntegrityResult {
        return if (featureFlags[AppCheckFeatureFlag.ENABLED]) {
            when (val result = appCheck.getAttestation()) {
                is AttestationResponse.Success -> AppIntegrityResult.Success(result.attestationJwt)
                is AttestationResponse.Failure -> AppIntegrityResult.Failure(result.reason)
                else -> AppIntegrityResult.Failure(result.toString())
            }
        } else {
            AppIntegrityResult.NotRequired
        }
    }
}
