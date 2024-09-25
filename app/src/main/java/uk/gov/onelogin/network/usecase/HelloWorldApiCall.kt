package uk.gov.onelogin.network.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.android.network.api.ApiFailureReason
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.onelogin.R
import uk.gov.onelogin.network.data.AuthApiResponse

interface HelloWorldApiCall {
    suspend fun happyPath(): AuthApiResponse
    suspend fun errorPath(): AuthApiResponse
}

class HelloWorldApiCallImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val httpClient: GenericHttpClient
) : HelloWorldApiCall {
    override suspend fun happyPath(): AuthApiResponse {
        val endpoint = context.getString(R.string.helloWorldEndpoint)
        val request = ApiRequest.Get(
            url = context.getString(R.string.helloWorldUrl, endpoint)
        )
        val response = httpClient.makeAuthorisedRequest(request, "sts-test.hello-world.read")
        return handleResponse(response)
    }

    override suspend fun errorPath(): AuthApiResponse {
        val endpoint = context.getString(R.string.helloWorldEndpoint) + "/error"
        val request = ApiRequest.Get(
            url = context.getString(R.string.helloWorldUrl, endpoint)
        )
        val response = httpClient.makeAuthorisedRequest(request, "sts-test.hello-world.read")

        return handleResponse(response)
    }

    private fun handleResponse(response: ApiResponse): AuthApiResponse {
        return when (response) {
            is ApiResponse.Failure ->
                when (response.reason) {
                    ApiFailureReason.AccessTokenExpired ->
                        AuthApiResponse.AuthExpired

                    ApiFailureReason.AuthFailed,
                    ApiFailureReason.AuthProviderNotInitialised,
                    ApiFailureReason.General,
                    ApiFailureReason.Non200Response ->
                        AuthApiResponse.Failure(
                            Exception(response.error.message ?: "Error")
                        )
                }

            is ApiResponse.Success<*> ->
                AuthApiResponse.Success(response.response.toString())

            ApiResponse.Loading ->
                AuthApiResponse.Failure(Exception("Loading"))

            ApiResponse.Offline ->
                AuthApiResponse.Failure(Exception("Offline"))
        }
    }
}
