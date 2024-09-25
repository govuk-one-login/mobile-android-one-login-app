package uk.gov.onelogin.network.data

sealed class AuthApiResponse {
    data class Failure(val e: Exception) : AuthApiResponse()
    data class Success<T>(val response: T) : AuthApiResponse()
    data object AuthExpired : AuthApiResponse()
}
