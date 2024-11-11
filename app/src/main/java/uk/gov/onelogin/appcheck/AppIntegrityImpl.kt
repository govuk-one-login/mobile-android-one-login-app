package uk.gov.onelogin.appcheck

import android.util.Log
import io.ktor.util.date.getTimeMillis
import javax.inject.Inject
import uk.gov.android.authentication.integrity.ClientAttestationManager
import uk.gov.android.authentication.integrity.model.AttestationResponse
import uk.gov.android.features.FeatureFlags
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.onelogin.appcheck.AppIntegrity.Companion.CLIENT_ATTESTATION
import uk.gov.onelogin.appcheck.AppIntegrity.Companion.CLIENT_ATTESTATION_EXPIRY
import uk.gov.onelogin.appcheck.AppIntegrity.Companion.SECURE_STORE_ERROR
import uk.gov.onelogin.features.AppCheckFeatureFlag
import uk.gov.onelogin.tokens.usecases.GetFromOpenSecureStore
import uk.gov.onelogin.tokens.usecases.SaveToOpenSecureStore

class AppIntegrityImpl @Inject constructor(
    private val featureFlags: FeatureFlags,
    private val appCheck: ClientAttestationManager,
    private val saveToOpenSecureStore: SaveToOpenSecureStore,
    private val getFromOpenSecureStore: GetFromOpenSecureStore
) : AppIntegrity {
    override suspend fun startCheck(): AppIntegrityResult {
        // This is just to not block from using the app as the Firebase Token expiry time is set for a longer time
        // which blocks from logging back in - to solve this needs deleting the app and re-install or waiting for > 15 min
        // Needs to be update with the proper logic on DCMAW-10441
        val expiry: String? = getFromOpenSecureStore.invoke(
            CLIENT_ATTESTATION_EXPIRY
        )
        return if (featureFlags[AppCheckFeatureFlag.ENABLED] && isAttestationExpired(expiry)) {
            val result = appCheck.getAttestation()
            Log.d("AppIntegrity", "$result")
            when (result) {
                is AttestationResponse.Success -> handleClientAttestation(result)
                is AttestationResponse.Failure -> AppIntegrityResult.Failure(result.reason)
            }
        } else {
            AppIntegrityResult.NotRequired
        }
    }

    private suspend fun handleClientAttestation(result: AttestationResponse.Success) =
        try {
            saveToOpenSecureStore.invoke(CLIENT_ATTESTATION, result.attestationJwt)
            saveToOpenSecureStore
                .invoke(CLIENT_ATTESTATION_EXPIRY, result.expiresIn.toString())
            AppIntegrityResult.Success(result.attestationJwt)
        } catch (e: SecureStorageError) {
            AppIntegrityResult.Failure(e.message ?: SECURE_STORE_ERROR)
        }

    private fun isAttestationExpired(expiryTime: String?): Boolean {
        if (expiryTime != null) {
            val result = expiryTime.toLong() <= getTimeMillis()
            return result
        } else {
            return true
        }
    }
}
