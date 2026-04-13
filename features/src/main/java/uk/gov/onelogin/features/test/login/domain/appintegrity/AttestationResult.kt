package uk.gov.onelogin.features.test.login.domain.appintegrity

sealed class AttestationResult {
    data class Success(
        val clientAttestation: String,
    ) : AttestationResult()

    data class Failure(
        val error: Throwable,
    ) : AttestationResult()

    data class NotRequired(
        val savedAttestation: String?,
    ) : AttestationResult()
}
