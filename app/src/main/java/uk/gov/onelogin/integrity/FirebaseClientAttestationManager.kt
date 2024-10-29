package uk.gov.onelogin.integrity

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
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
        val res: MutableStateFlow<AttestationResponse> = MutableStateFlow(AttestationResponse.Loading)
        try {
            appChecker.getAppCheckToken(
                onSuccess = { result ->
                    Log.d("SuccessFirebaseToken", result)
                    res.value = AttestationResponse.Success(result)
                },
                onFailure = { error ->
                    Log.e("FailureFirebaseToken", error.toString())
                    res.value = AttestationResponse.Failure(error.message.toString(), error)
                }
            )
        } catch (e: Exception) {
            res.value = AttestationResponse.Failure(e.toString())
        }
        Log.d("FirebaseTokenResult", "${res.value}")
        return AttestationResponse.Success(res.value.toString())
    }

    override suspend fun signAttestation(attestation: String): SignedResponse {
        // Not yet implemented
        return SignedResponse.Failure("Not yet implemented")
    }
}
