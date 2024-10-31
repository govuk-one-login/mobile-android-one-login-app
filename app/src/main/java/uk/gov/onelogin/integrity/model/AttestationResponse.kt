package uk.gov.onelogin.integrity.model

sealed class AttestationResponse {
    data class Success(val attestationJwt: String) : AttestationResponse()
    data class Failure(val reason: String, val error: Throwable? = null) : AttestationResponse()
}
