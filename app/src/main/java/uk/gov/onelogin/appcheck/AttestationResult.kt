package uk.gov.onelogin.appcheck

sealed class AttestationResult {
    data object Success : AttestationResult()
    data class Failure(val error: String) : AttestationResult()
    data object NotRequired : AttestationResult()
}
