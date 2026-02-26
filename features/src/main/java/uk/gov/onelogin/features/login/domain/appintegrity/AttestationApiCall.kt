package uk.gov.onelogin.features.login.domain.appintegrity

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import uk.gov.android.authentication.integrity.appcheck.model.AttestationResponse
import uk.gov.android.authentication.integrity.appcheck.usecase.AttestationCaller
import uk.gov.android.authentication.json.jwk.JWK
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.onelogin.core.R
import javax.inject.Inject

class AttestationApiCall
    @Inject
    constructor(
        @param:ApplicationContext
        private val context: Context,
        private val httpClient: GenericHttpClient,
    ) : AttestationCaller {
        override suspend fun call(
            token: String,
            jwk: JWK.JsonWebKey,
        ): AttestationResponse {
            val endpoint = context.getString(R.string.clientAttestationEndpoint)
            val request =
                ApiRequest.Post(
                    url = context.getString(R.string.webBaseUrl, endpoint) + "?device=android",
                    body = jwk,
                    headers =
                        listOf(
                            AttestationCaller.FIREBASE_HEADER to token,
                            AttestationCaller.CONTENT_TYPE to AttestationCaller.CONTENT_TYPE_VALUE,
                        ),
                )
            return when (val apiResponse = httpClient.makeRequest(request)) {
                is ApiResponse.Success<*> -> handleResponse(apiResponse)
                is ApiResponse.Failure -> {
                    // Error mappings - see Errors returned by Mobile Platform BackEnd:
                    // https://govukverify.atlassian.net/wiki/spaces/DCMAW/pages/3787195450/GOV.UK+One+Login+app+-+Error+handling#App-integrity-check-failures
                    val expType =
                        when (apiResponse.status) {
                            INVALID_PUBLIC_KEY_JWK
                            -> AppIntegrity.AppIntegrityException.AppIntegrityErrorType.APP_CHECK_FAILED
                            SERVER_ERROR, INVALID_APP_CHECK_TOKEN, INTERMITTENT_SERVER_ERROR
                            -> AppIntegrity.AppIntegrityException.AppIntegrityErrorType.INTERMITTENT
                            // This should never be reached as per guidance
                            else -> AppIntegrity.AppIntegrityException.AppIntegrityErrorType.GENERIC
                        }
                    val exp = AppIntegrity.AppIntegrityException.ClientAttestationException(apiResponse.error, expType)
                    AttestationResponse.Failure(
                        exp.e.message ?: NETWORK_ERROR,
                        exp,
                    )
                }

                // This is for ApiResponse.Offline and ApiResponse.Loading which is never used
                else ->
                    AttestationResponse.Failure(
                        NETWORK_ERROR,
                        AppIntegrity.AppIntegrityException.ClientAttestationException(
                            Exception(NETWORK_ERROR)
                        )
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
                    e,
                )
            }

        companion object {
            const val NETWORK_ERROR = "Network error"
            const val JSON_DECODE_ERROR = "ERROR: Decode AttestationResponse.Success error"

            internal const val INVALID_PUBLIC_KEY_JWK = 400
            internal const val SERVER_ERROR = 401
            internal const val INVALID_APP_CHECK_TOKEN = SERVER_ERROR
            internal const val INTERMITTENT_SERVER_ERROR = 500
        }
    }
