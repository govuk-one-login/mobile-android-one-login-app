package uk.gov.onelogin.features.login.domain.appintegrity

import uk.gov.android.authentication.integrity.pop.SignedPoP

interface AppIntegrity {
    suspend fun getClientAttestation(): AttestationResult

    fun getProofOfPossession(): SignedPoP

    suspend fun retrieveSavedClientAttestation(): String?

    companion object {
        const val CLIENT_ATTESTATION = "appCheckClientAttestation"
        const val CLIENT_ATTESTATION_EXPIRY = "appCheckClientAttestationExpiry"
        const val SECURE_STORE_ERROR = "ERROR: Saving to open secure store error"

        data class FirebaseException(
            val e: Throwable,
        ) : Exception(e)

        class ClientAttestationException : Exception {
            constructor(e: Throwable) : super(e)
            constructor(msg: String) : super(msg)
        }

        data class ProofOfPossessionException(
            val e: Throwable?,
        ) : Exception(e)
    }
}
