package uk.gov.onelogin.integrity.model

sealed class SignedResponse {
    data class Success(val signedAttestationJwt: String) : SignedResponse()
    data class Failure(val reason: String, val error: Throwable? = null) : SignedResponse()
}
