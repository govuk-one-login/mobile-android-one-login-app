package uk.gov.onelogin.integrity

import android.util.Log
import uk.gov.onelogin.integrity.appcheck.AppChecker
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
        var response: AttestationResponse = AttestationResponse.Loading
        appChecker.getAppCheckToken(
            onSuccess = { token ->
                Log.d("FirebaseToken", token)
                response = AttestationResponse.Success(token)
            },
            onFailure = { error ->
                Log.e("FirebaseToken", error.toString())
                response = AttestationResponse.Failure(error.message.toString(), error)
            }
        )
        Log.d("FirebaseTokenResult", "$response")
        return response
    }

    override suspend fun signAttestation(attestation: String): SignedResponse {
        // Not yet implemented
        return SignedResponse.Failure("Not yet implemented")
    }
}
