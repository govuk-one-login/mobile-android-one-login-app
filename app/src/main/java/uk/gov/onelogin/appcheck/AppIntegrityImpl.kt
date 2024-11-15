package uk.gov.onelogin.appcheck

import android.content.Context
import android.util.Log
import io.ktor.util.date.getTimeMillis
import javax.inject.Inject
import uk.gov.android.authentication.integrity.ClientAttestationManager
import uk.gov.android.authentication.integrity.appcheck.model.AttestationResponse
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.features.FeatureFlags
import uk.gov.android.onelogin.R
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.onelogin.appcheck.AppIntegrity.Companion.CLIENT_ATTESTATION
import uk.gov.onelogin.appcheck.AppIntegrity.Companion.CLIENT_ATTESTATION_EXPIRY
import uk.gov.onelogin.appcheck.AppIntegrity.Companion.SECURE_STORE_ERROR
import uk.gov.onelogin.features.AppCheckFeatureFlag
import uk.gov.onelogin.tokens.usecases.GetFromOpenSecureStore
import uk.gov.onelogin.tokens.usecases.SaveToOpenSecureStore

class AppIntegrityImpl @Inject constructor(
    private val context: Context,
    private val featureFlags: FeatureFlags,
    private val appCheck: ClientAttestationManager,
    private val saveToOpenSecureStore: SaveToOpenSecureStore,
    private val getFromOpenSecureStore: GetFromOpenSecureStore
) : AppIntegrity {
    override suspend fun getClientAttestation(): AttestationResult {
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
                is AttestationResponse.Failure -> AttestationResult.Failure(result.reason)
            }
        } else {
            AttestationResult.NotRequired
        }
    }

    override fun getProofOfPossession(): SignedPoP {
        return appCheck.generatePoP(
            iss = context.getString(R.string.stsClientId),
            aud = context.getString(R.string.baseStsUrl)
        )
    }

    private suspend fun handleClientAttestation(result: AttestationResponse.Success) =
        try {
            saveToOpenSecureStore.save(CLIENT_ATTESTATION, result.attestationJwt)
            saveToOpenSecureStore
                .save(CLIENT_ATTESTATION_EXPIRY, result.expiresIn)
            AttestationResult.Success
        } catch (e: SecureStorageError) {
            AttestationResult.Failure(e.message ?: SECURE_STORE_ERROR)
        }

    private fun isAttestationExpired(expiryTime: String?): Boolean {
        return if (expiryTime != null) {
            val result = expiryTime.toLong() <= getTimeMillis()
            result
        } else {
            true
        }
    }
}
