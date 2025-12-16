package uk.gov.onelogin.core.network.domain

import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.Parameters
import kotlinx.serialization.json.Json
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.api.ApiResponseException
import uk.gov.android.network.auth.AuthenticationProvider
import uk.gov.android.network.auth.AuthenticationResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.LoginException
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.utils.AccessToken

class StsAuthenticationProvider(
    private val stsUrl: String,
    private val tokenRepository: TokenRepository,
    @AccessToken
    private val isAccessTokenExpired: IsTokenExpired,
    private val httpClient: GenericHttpClient,
    private val navigator: Navigator,
    private val logger: Logger
) : AuthenticationProvider {
    @Suppress("TooGenericExceptionCaught")
    override suspend fun fetchBearerToken(scope: String): AuthenticationResponse {
        val jsonDecoder = Json { ignoreUnknownKeys = true }

        if (isAccessTokenExpired()) {
            if (tokenRepository.shouldNavigateToReAuth()) {
                navigator.navigate(SignOutRoutes.Info)
            }
            return AuthenticationResponse.Failure(ApiResponseException("Access token expired"))
        }

        return tokenRepository.getTokenResponse()?.accessToken?.let {
            val request = ApiRequest.Post(
                url = stsUrl,
                body = FormDataContent(
                    Parameters.build {
                        append(
                            GRANT_TYPE,
                            "urn:ietf:params:oauth:grant-type:token-exchange"
                        )
                        append(SUBJECT_TOKEN, it)
                        append(
                            SUBJECT_TOKEN_TYPE,
                            "urn:ietf:params:oauth:token-type:access_token"
                        )
                        append(SCOPE, scope)
                    }
                ),
                headers = listOf(
                    Pair("Content-Type", "application/x-www-form-urlencoded")
                )
            )

            val response = httpClient.makeRequest(request)
            if (response is ApiResponse.Success<*>) {
                try {
                    val tokenResponseString: String = response.response.toString()
                    val tokenApiResponse: TokenApiResponse = jsonDecoder
                        .decodeFromString(tokenResponseString)
                    AuthenticationResponse.Success(tokenApiResponse.token)
                } catch (e: Exception) {
                    val loginException = LoginException(e)
                    logger.error(
                        loginException::class.java.simpleName,
                        e.message.toString(),
                        loginException
                    )
                    AuthenticationResponse.Failure(e)
                }
            } else {
                AuthenticationResponse.Failure(Exception("Failed to fetch service token"))
            }
        } ?: AuthenticationResponse.Failure(Exception("No access token"))
    }

    companion object {
        private const val GRANT_TYPE = "grant_type"
        private const val SUBJECT_TOKEN = "subject_token"
        private const val SUBJECT_TOKEN_TYPE = "subject_token_type"
        private const val SCOPE = "scope"
    }
}
