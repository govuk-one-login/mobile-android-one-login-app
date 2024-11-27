package uk.gov.onelogin.appcheck

import android.content.Context
import android.util.Log
import io.ktor.util.date.getTimeMillis
import javax.inject.Inject
import uk.gov.android.authentication.integrity.AppIntegrityManager
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
    private val appCheck: AppIntegrityManager,
    private val saveToOpenSecureStore: SaveToOpenSecureStore,
    private val getFromOpenSecureStore: GetFromOpenSecureStore
) : AppIntegrity {
    override suspend fun getClientAttestation(): AttestationResult {
        return if (isAttestationCallRequired()) {
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
            appCheck.getExpiry(result.attestationJwt)?.let {
                saveToOpenSecureStore.save(CLIENT_ATTESTATION_EXPIRY, it)
            }
            AttestationResult.Success
        } catch (e: SecureStorageError) {
            AttestationResult.Failure(e.message ?: SECURE_STORE_ERROR)
        }

    private suspend fun isAttestationCallRequired(): Boolean {
        val expiry: String? = getFromOpenSecureStore.invoke(
            CLIENT_ATTESTATION_EXPIRY
        )
        val clientAttestation: String? = getFromOpenSecureStore.invoke(
            CLIENT_ATTESTATION
        )

        println("isAttestationExpired: ${isAttestationExpired(expiry)}")
        println("isAttestationNull: ${clientAttestation == null}")
        println(
            "isAttestationVerified: ${
                clientAttestation?.let {
                    appCheck.verifyAttestationJwk(clientAttestation)
                }
            }"
        )

        val result = isAttestationExpired(expiry) ||
            clientAttestation == null ||
            !appCheck.verifyAttestationJwk(clientAttestation)

        return featureFlags[AppCheckFeatureFlag.ENABLED] && result
    }

    private fun isAttestationExpired(expiryTime: String?): Boolean {
        return if (!expiryTime.isNullOrEmpty()) {
            expiryTime.toLong() <= getTimeMillis() / CONVERT_TO_SECONDS
        } else {
            true
        }
    }

    companion object {
        private const val CONVERT_TO_SECONDS = 1000
    }
}
