package uk.gov.onelogin.login.usecase

import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.onelogin.tokens.verifier.JwtVerifier

interface VerifyIdToken {
    suspend operator fun invoke(idToken: String): Boolean
}

class VerifyIdTokenImpl(
    private val jwksUrl: String,
    private val httpClient: GenericHttpClient,
    private val verifier: JwtVerifier
) : VerifyIdToken {
    override suspend fun invoke(idToken: String): Boolean {
        var verified = false
        val response = httpClient.makeRequest(
            ApiRequest.Get(jwksUrl)
        )

        if (response is ApiResponse.Success<*>) {
            verified = verifier.verify(
                idToken,
                response.response.toString()
            )
        }

        return verified
    }
}
