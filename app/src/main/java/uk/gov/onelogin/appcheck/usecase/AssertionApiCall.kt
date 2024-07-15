package uk.gov.onelogin.appcheck.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.onelogin.R

interface AssertionApiCall {
    suspend operator fun invoke(firebaseToken: String): String
}

class AssertionApiCallImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val httpClient: GenericHttpClient
) : AssertionApiCall {
    override suspend fun invoke(firebaseToken: String): String {
        val endpoint = context.getString(R.string.assertionEndpoint)
        val request = ApiRequest.Get(
            url = context.getString(R.string.assertionUrl, endpoint),
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
