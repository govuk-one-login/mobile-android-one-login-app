package uk.gov.onelogin.integrity

import android.util.Log
import uk.gov.onelogin.integrity.appcheck.AppChecker
import uk.gov.onelogin.integrity.model.AppCheckToken
import uk.gov.onelogin.integrity.model.AppIntegrityConfiguration
import uk.gov.onelogin.integrity.model.AttestationResponse
import uk.gov.onelogin.integrity.model.SignedResponse

@Suppress("UnusedPrivateProperty")
class FirebaseClientAttestationManager(
    config: AppIntegrityConfiguration
) : ClientAttestationManager {
    private val appChecker: AppChecker = config.appChecker
    private val keyManager = KeystoreManager()

    override suspend fun getAttestation(): AttestationResponse {
        // Get Firebase token
        val token = appChecker.getAppCheckToken().getOrElse { err ->
            AttestationResponse.Failure(err.toString())
        }
        Log.d("Token", "$token")
        // If successful -> functionality to get signed attestation form Mobile back-end
        return if (token is AppCheckToken) {
            AttestationResponse.Success(attestationJwt = token.jwtToken)
        // If unsuccessful -> return the failure
        } else {
            token as AttestationResponse.Failure
        }
    }

    override suspend fun signAttestation(attestation: String): SignedResponse {
        // Not yet implemented
        return SignedResponse.Failure("Not yet implemented")
    }
}
