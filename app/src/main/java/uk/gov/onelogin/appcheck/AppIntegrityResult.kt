package uk.gov.onelogin.appcheck

sealed class AppIntegrityResult {
    data class Success(val firebaseJwt: String) : AppIntegrityResult()
    data class Failure(val error: String) : AppIntegrityResult()
    data object NotRequired : AppIntegrityResult()
}