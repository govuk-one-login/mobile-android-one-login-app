package uk.gov.onelogin.appcheck.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import uk.gov.android.authentication.integrity.model.AttestationResponse
import javax.inject.Inject
import uk.gov.android.authentication.integrity.usecase.AttestationCaller
import uk.gov.android.authentication.integrity.usecase.JWK
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.onelogin.R

class AttestationApiCall @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val httpClient: GenericHttpClient
) : AttestationCaller {
    override suspend fun call(
        firebaseToken: String,
        jwk: JWK.JsonWebKey
    ): AttestationResponse {
        val endpoint = context.getString(R.string.assertionEndpoint)
        val request = ApiRequest.Post(
            url = context.getString(R.string.webBaseUrl, endpoint) + "?device=android",
            body = jwk,
            headers = listOf(
                AttestationCaller.FIREBASE_HEADER to firebaseToken,
                AttestationCaller.CONTENT_TYPE to AttestationCaller.CONTENT_TYPE_VALUE
            )
        )
        val apiResponse = httpClient.makeRequest(request)
        return if (apiResponse is ApiResponse.Success<*>) {
            handleResponse(apiResponse)
        } else {
            AttestationResponse.Failure(
                (apiResponse as ApiResponse.Failure).error.message ?: NETWORK_ERROR,
                apiResponse.error
            )
        }
    }

    private fun handleResponse(apiResponse: ApiResponse) =
        try {
            val response = (apiResponse as ApiResponse.Success<String>).response
            Json.decodeFromString<AttestationResponse.Success>(response)
        } catch (e: IllegalArgumentException) {
            AttestationResponse.Failure(
                e.message ?: JSON_DECODE_ERROR,
                e
            )
        }

    companion object {
        const val NETWORK_ERROR = "Network error"
        const val JSON_DECODE_ERROR = "ERROR: Decode AttestationResponse.Success error"
    }
}
