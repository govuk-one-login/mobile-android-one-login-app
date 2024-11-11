package uk.gov.onelogin.appcheck

import android.util.Log
import javax.inject.Inject
import uk.gov.android.authentication.integrity.ClientAttestationManager
import uk.gov.android.authentication.integrity.model.AttestationResponse
import uk.gov.android.features.FeatureFlags
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.onelogin.appcheck.AppIntegrity.Companion.CLIENT_ATTESTATION
import uk.gov.onelogin.appcheck.AppIntegrity.Companion.CLIENT_ATTESTATION_EXPIRY
import uk.gov.onelogin.appcheck.AppIntegrity.Companion.SECURE_STORE_ERROR
import uk.gov.onelogin.features.AppCheckFeatureFlag
import uk.gov.onelogin.tokens.usecases.SaveToOpenSecureStore

class AppIntegrityImpl @Inject constructor(
    private val featureFlags: FeatureFlags,
    private val appCheck: ClientAttestationManager,
    private val saveToOpenSecureStore: SaveToOpenSecureStore
) : AppIntegrity {
    override suspend fun startCheck(): AppIntegrityResult {
        return if (featureFlags[AppCheckFeatureFlag.ENABLED]) {
            val result = appCheck.getAttestation()
            Log.d("AppIntegrity", "$result")
            when (result) {
                is AttestationResponse.Success -> {
                    try {
                        saveToOpenSecureStore.invoke(CLIENT_ATTESTATION, result.attestationJwt)
                        saveToOpenSecureStore
                            .invoke(CLIENT_ATTESTATION_EXPIRY, result.expiresIn.toString())
                        AppIntegrityResult.Success(result.attestationJwt)
                    } catch (e: SecureStorageError) {
                        AppIntegrityResult.Failure(e.message ?: SECURE_STORE_ERROR)
                    }
                }
                is AttestationResponse.Failure -> AppIntegrityResult.Failure(result.reason)
            }
        } else {
            AppIntegrityResult.NotRequired
        }
    }
}
