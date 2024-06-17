package uk.gov.onelogin.network.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.onelogin.R

interface HelloWorldApiCall {
    suspend fun happyPath(): String
    suspend fun errorPath(): String
}

class HelloWorldApiCallImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val httpClient: GenericHttpClient
) : HelloWorldApiCall {
    override suspend fun happyPath(): String {
        val endpoint = context.getString(R.string.helloWorldEndpoint)
        val request = ApiRequest.Get(
            url = context.getString(R.string.helloWorldUrl, endpoint)
        )
        val response = httpClient.makeAuthorisedRequest(request, "sts-test.hello-world.read")
        return if (response is ApiResponse.Success<*>) {
            response.response.toString()
        } else {
            (response as ApiResponse.Failure).error.message ?: "Error"
        }
    }

    override suspend fun errorPath(): String {
        val endpoint = context.getString(R.string.helloWorldEndpoint) + "/error"
        val request = ApiRequest.Get(
            url = context.getString(R.string.helloWorldUrl, endpoint)
        )
        val response = httpClient.makeAuthorisedRequest(request, "sts-test.hello-world.read")
        return if (response is ApiResponse.Success<*>) {
            response.response.toString()
        } else {
            (response as ApiResponse.Failure).error.message ?: "Error"
        }
    }
}
