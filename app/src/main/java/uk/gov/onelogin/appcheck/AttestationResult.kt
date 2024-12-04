package uk.gov.onelogin.appcheck

sealed class AttestationResult {
    data class Success(val clientAttestation: String) : AttestationResult()
    data class Failure(val error: String) : AttestationResult()
    data class NotRequired(val savedAttestation: String?) : AttestationResult()
}
