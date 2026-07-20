package uk.gov.onelogin.core.network.domain

interface HelloWorldApiCall {
    suspend fun authenticatedRequest(): String

    suspend fun authenticatedErrorRequest(): String

    suspend fun appIntegrityRequest(): String
}
