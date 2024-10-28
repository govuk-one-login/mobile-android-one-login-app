package uk.gov.onelogin.integrity.appcheck.usecase

import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient

internal fun interface AttestationApiCaller {
    suspend fun call(
        firebaseToken: String,
        backendUrl: String
    ): String
}

internal class AttestationApiCallerImpl(
    private val httpClient: GenericHttpClient
) : AttestationApiCaller {
    override suspend fun call(firebaseToken: String, backendUrl: String): String {
        val request = ApiRequest.Get(
            url = backendUrl,
            headers = listOf(
                "X-Firebase-Token" to firebaseToken
            )
        )
        val response = httpClient.makeRequest(request)
        return if (response is ApiResponse.Success<*>) {
            response.response.toString()
        } else {
            (response as ApiResponse.Failure).error.message ?: "Error"
        }
    }
}
