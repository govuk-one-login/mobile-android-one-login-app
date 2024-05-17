package uk.gov.onelogin.network.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.onelogin.R

interface HelloWorldApiCall {
    suspend operator fun invoke(): String
}

class HelloWorldApiCallImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val httpClient: GenericHttpClient
) : HelloWorldApiCall {
    override suspend fun invoke(): String {
        val endpoint = context.getString(R.string.helloWorldEndpoint)
        val request = ApiRequest.Get(
            url = context.getString(R.string.helloWorldUrl, endpoint)
        )
        val response = httpClient.makeAuthorisedRequest(request, "sts-test.hello-world.read")
        return if (response is ApiResponse.Success<*>) {
            (response as ApiResponse.Success<String>).response
        } else {
            (response as ApiResponse.Failure).error.message ?: "Error"
        }
    }
}
