package uk.gov.onelogin.features.login.domain.appintegrity

sealed class AttestationResult {
    data class Success(
        val clientAttestation: String,
    ) : AttestationResult()

    data class Failure(
        val error: Throwable,
    ) : AttestationResult()

    /**
     * @property savedAttestation The cached client attestation.
     *   This may be null if both of the following are true:
     *   - there is no cached client attestation
     *   - client attestation isn't required because the App Check feature is disabled
     */
    data class NotRequired(
        val savedAttestation: String?,
    ) : AttestationResult()
}
