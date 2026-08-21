package uk.gov.onelogin.core.network.domain

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.network.api.v2.ApiRequest
import uk.gov.android.network.api.v3.ApiResponse
import uk.gov.android.network.service.v2.NetworkService
import uk.gov.android.network.service.v2.NetworkServiceResponse
import uk.gov.android.onelogin.core.R
import javax.inject.Inject

class HelloWorldApiCallImpl
    @Inject
    constructor(
        @ApplicationContext
        private val context: Context,
        private val networkService: NetworkService,
    ) : HelloWorldApiCall {
        override suspend fun happyPath(): String {
            val endpoint = context.getString(R.string.helloWorldEndpoint)
            val request =
                ApiRequest.Get(
                    url = context.getString(R.string.helloWorldUrl, endpoint),
                )
            val response = networkService.makeRequest(request) {
                withAuthentication(scope = "sts-test.hello-world.read")
            }
            return response.toDisplay()
        }

        override suspend fun errorPath(): String {
            val endpoint = context.getString(R.string.helloWorldEndpoint) + "/error"
            val request =
                ApiRequest.Get(
                    url = context.getString(R.string.helloWorldUrl, endpoint),
                )
            val response = networkService.makeRequest(request) {
                withAuthentication(scope = "sts-test.hello-world.read")
            }

            return response.toDisplay()
        }

        private fun NetworkServiceResponse.toDisplay() =
            when (this) {
                is ApiResponse.Failure -> error.message
                is ApiResponse.Success -> body
            }
    }
