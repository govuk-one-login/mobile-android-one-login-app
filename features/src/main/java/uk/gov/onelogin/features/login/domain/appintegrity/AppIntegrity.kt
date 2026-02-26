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


    }

    sealed class AppIntegrityException(
        open val e: Throwable,
        open val type: AppIntegrityErrorType
    ) : Exception(e) {
        data class FirebaseException(
            override val e: Throwable,
            override val type: AppIntegrityErrorType = AppIntegrityErrorType.GENERIC
        ) : AppIntegrityException(e, type)

        data class ClientAttestationException(
            override val e: Throwable,
            override val type: AppIntegrityErrorType = AppIntegrityErrorType.GENERIC
        ) : AppIntegrityException(e, type)

        data class ProofOfPossessionException(
            override val e: Throwable,
            override val type: AppIntegrityErrorType = AppIntegrityErrorType.GENERIC
        ) : AppIntegrityException(e, type)

        class Generic(
            override val e: Throwable,
            override val type: AppIntegrityErrorType = AppIntegrityErrorType.INTERMITTENT
        ) : AppIntegrityException(e, type)

        enum class AppIntegrityErrorType {
            INTERMITTENT, APP_CHECK_FAILED, GENERIC
        }
    }
}
