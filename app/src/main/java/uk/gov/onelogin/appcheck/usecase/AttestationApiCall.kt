package uk.gov.onelogin.appcheck.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.onelogin.R
import uk.gov.onelogin.integrity.appcheck.usecase.AttestationCaller

class AttestationApiCall @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val httpClient: GenericHttpClient
) : AttestationCaller {
    override suspend fun call(
        signedProofOfPossession: String,
        jwkX: String,
        jwkY: String
    ): Result<AttestationCaller.Response> {
        val endpoint = context.getString(R.string.assertionEndpoint)
        val request = ApiRequest.Get(
            url = context.getString(R.string.assertionUrl, endpoint) + "?device=android",
            headers = listOf(
                "X-Firebase-Token" to AttestationCaller.FIREBASE_HEADER
            )
        )
        val response = httpClient.makeRequest(request)
        return if (response is ApiResponse.Success<*>) {
            Result.success(
                AttestationCaller.Response(
                    jwt = response.response.toString(),
                    expiresIn = 0
                )
            )
        } else {
            Result.failure((response as ApiResponse.Failure).error)
        }
    }
}
