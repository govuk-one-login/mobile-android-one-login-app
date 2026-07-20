package uk.gov.onelogin.core.network.domain

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.network.api.v2.ApiRequest
import uk.gov.android.network.api.v2.ApiResponse
import uk.gov.android.network.service.NetworkService
import uk.gov.android.onelogin.core.R
import javax.inject.Inject
import kotlin.toString

class HelloWorldApiCallImpl
@Inject
constructor(
    @ApplicationContext
    private val context: Context,
    private val networkService: NetworkService,
) : HelloWorldApiCall {
    override suspend fun authenticatedRequest(): String =
        request(
            endpoint = context.getString(R.string.helloWorldEndpoint),
            withAuthentication = true,
        )

    override suspend fun authenticatedErrorRequest(): String {
        return request(
            endpoint = context.getString(R.string.helloWorldEndpoint) + "/error",
            withAuthentication = true,
        )
    }

    override suspend fun appIntegrityRequest(): String =
        request(
            endpoint = context.getString(R.string.helloWorldEndpoint),
            withAttestation = true,
            withRefreshDPoP = true
        )

    private suspend fun request(
        endpoint: String,
        withAuthentication: Boolean = false,
        withAttestation: Boolean = false,
        withRefreshDPoP: Boolean = false,
    ): String {
        val request =
            ApiRequest.Get(
                url = context.getString(R.string.helloWorldUrl, endpoint),
            )
        val response = networkService.makeRequest(request) {
            if (withAuthentication) {
                withAuthentication("sts-test.hello-world.read")
            }
            this.withAttestation = withAttestation
            this.withRefreshDPoP = withRefreshDPoP
        }

        return when (response) {
            is ApiResponse.Failure -> response.error.message ?: "Error"
            is ApiResponse.Success<*> -> response.response.toString()
        }
    }
}
