package uk.gov.onelogin.features.login.domain.appintegrity

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.authentication.integrity.appcheck.model.AttestationResponse
import uk.gov.android.authentication.integrity.appcheck.usecase.AttestationCaller
import uk.gov.android.authentication.json.jwk.JWK
import uk.gov.android.network.api.v2.ApiRequest
import uk.gov.android.network.api.v3.ApiResponse
import uk.gov.android.network.client.ContentType
import uk.gov.android.network.service.v2.NetworkService
import uk.gov.android.network.service.v2.NetworkServiceTypedSuccessExt.makeRequest
import uk.gov.android.onelogin.core.R
import javax.inject.Inject

class AttestationApiCall
    @Inject
    constructor(
        @param:ApplicationContext
        private val context: Context,
        private val networkService: NetworkService,
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
                        ),
                    contentType = ContentType.APPLICATION_JSON,
                )
            return when (val apiResponse = networkService.makeRequest<AttestationResponse.Success>(request)) {
                is ApiResponse.Success -> apiResponse.body

                is ApiResponse.Failure -> {
                    // Error mappings - see Errors returned by Mobile Platform BackEnd:
                    // https://govukverify.atlassian.net/wiki/spaces/DCMAW/pages/3787195450/GOV.UK+One+Login+app+-+Error+handling#App-integrity-check-failures
                    val expType =
                        when (apiResponse.status) {
                            INVALID_PUBLIC_KEY_JWK
                                -> AppIntegrityException.AppIntegrityErrorType.APP_CHECK_FAILED

                        SERVER_ERROR, INVALID_APP_CHECK_TOKEN, INTERMITTENT_SERVER_ERROR
                            -> AppIntegrityException.AppIntegrityErrorType.INTERMITTENT
                        // This should never be reached as per guidance
                        else -> AppIntegrityException.AppIntegrityErrorType.GENERIC
                    }

                    val exp = AppIntegrityException.ClientAttestationException(apiResponse.error, expType)
                    AttestationResponse.Failure(
                        exp.e.message ?: NETWORK_ERROR,
                        exp,
                    )
                }
            }
        }

        companion object {
            const val NETWORK_ERROR = "Network error"

            internal const val INVALID_PUBLIC_KEY_JWK = 400
            internal const val SERVER_ERROR = 401
            internal const val INVALID_APP_CHECK_TOKEN = SERVER_ERROR
            internal const val INTERMITTENT_SERVER_ERROR = 500
        }
    }
