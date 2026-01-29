package uk.gov.onelogin.features.network.provider

import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.Parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
import uk.gov.onelogin.core.network.domain.TokenApiResponse
import uk.gov.onelogin.core.tokens.data.LoginException
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.utils.AccessToken
import uk.gov.onelogin.core.utils.ActivityProvider
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchange
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchangeResult
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

/**
 * [uk.gov.onelogin.features.network.provider.StsAuthenticationProvider] provides an implementation of the [AuthenticationProvider]
 * that is used by a [GenericHttpClient] to enable authenticated requests.
 *
 * @param activityProvider provides access to a FragmentActivity required when attempting to retrieve tokens form the secure store for the [android.hardware.biometrics.BiometricPrompt]
 * @param stsUrl provides the STS endpoint used to make a service token exchange
 * @param tokenRepository provides the access token required for the service token exchange
 * @param isAccessTokenExpired provides functionality to conduct a check and determine is a refresh exchange is required when the access token is expired
 * @param httpClient it's a network client required for making the network requests to the service token endpoint
 * @param navigator provides a [Navigator] to allow navigating to error screens where required (e.g. Re-Authentication, Sign-In Required)
 * @param signOutUseCase provides functionality to delete all data linked to a session/ user when a Sign-In is required (e.g. missing Persistent Session ID)
 * @param logger provides a [Logger] to enable logging errors to Crashlytics
 *
 */
@Suppress("LongParameterList")
class StsAuthenticationProvider(
    private val activityProvider: ActivityProvider,
    private val stsUrl: String,
    private val tokenRepository: TokenRepository,
    @param:AccessToken
    private val isAccessTokenExpired: IsTokenExpired,
    private val httpClient: GenericHttpClient,
    private val navigator: Navigator,
    private val refreshExchange: RefreshExchange,
    private val signOutUseCase: SignOutUseCase,
    private val logger: Logger
) : AuthenticationProvider {
    private val jsonDecoder = Json { ignoreUnknownKeys = true }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun fetchBearerToken(scope: String): AuthenticationResponse {
        // Check if access token is expired
        return if (isAccessTokenExpired()) {
            // If it is, attempt refresh exchange
            val refreshStatus = attemptRefreshExchangeAndGetResult()

            // Handle refresh exchange result
            when (refreshStatus) {
                // Prompt for re-authentication
                is RefreshExchangeResult.ReAuthRequired,
                RefreshExchangeResult.ClientAttestationFailure -> {
                    navigator.navigate(SignOutRoutes.Info)
                    AuthenticationResponse.Failure(ApiResponseException(REFRESH_EXCHANGE_ERROR_MSG))
                }

                // If Manual Sign In required, delete all data, navigate to Re Auth Error screen to then
                // allow for seeing the Welcome Screen (sign in) and return an error to the consumer
                is RefreshExchangeResult.SignInRequired -> {
                    signOutUseCase.invoke()
                    navigator.navigate(SignOutRoutes.ReAuthError)
                    AuthenticationResponse.Failure(ApiResponseException(MANUAL_SIGN_IN_REQUIRED_ERROR_MSG))
                }

                // If success continue and attempt to get a service token
                // If user cancelled bio prompt/ bio check failed/ offline, allow for the consumer to handle the error
                // which means treating it as a success and it will fail at getting the access token which will be
                // treated as a error on the consumer/ SDKs side
                else -> {
                    attemptServiceTokenExchange(scope)
                }
            }
            // If access token valid, attempt to get a service token
        } else {
            attemptServiceTokenExchange(scope)
        }
    }

    /**
     * This is required/ called only if the access token is expired.
     */
    private suspend fun attemptRefreshExchangeAndGetResult(): RefreshExchangeResult {
        var refreshExchangeResult: RefreshExchangeResult = RefreshExchangeResult.ReAuthRequired
        // If an activity is available, it will attempt the refresh exchange
        // Will display the Re-Auth screen if activity is not available
        // Require dispatcher because the BiometricPrompt needs to be called from Main Thread
        withContext(Dispatchers.Main.immediate) {
            // Check if activity is null
            activityProvider.getCurrentActivity()?.let {
                // If not null, continue and attempt the refresh exchange
                refreshExchange.getTokens(context = it) { result ->
                    refreshExchangeResult = result
                }
            } ?: run {
                // If activity is null, log error
                val error = FragmentActivityNull()
                logger.error(
                    this@StsAuthenticationProvider.javaClass.simpleName,
                    error.msg,
                    error
                )
                // AND require re-authentication as the refresh exchange will fail
                refreshExchangeResult = RefreshExchangeResult.ReAuthRequired
            }
        }
        return refreshExchangeResult
    }

    /**
     * Attempts the service token request and handles the result.
     * @param scope the scope of teh service token that is required/ requested
     * @return [AuthenticationResponse] which returns a response either containing a failure containing a message or
     * a success containing the service token issues
     */
    private suspend fun attemptServiceTokenExchange(scope: String): AuthenticationResponse =
        tokenRepository.getTokenResponse()?.accessToken?.let {
            val request = createServiceTokenRequest(it, scope)
            val response = httpClient.makeRequest(request)
            handleServiceTokenResponse(response)
        } ?: AuthenticationResponse.Failure(Exception(NO_ACCESS_TOKEN_ERROR_MSG))

    /**
     * Creates the [ApiRequest.Post] to get a service token - - see [attemptServiceTokenExchange].
     * @param accessToken the jwt that will be presented in exchange for a service token which enforces authentication of the client
     * @param scope the scope of teh service token that is required/ requested
     * @return [ApiRequest.Post]
     */
    private fun createServiceTokenRequest(
        accessToken: String,
        scope: String
    ): ApiRequest.Post<Any> =
        ApiRequest.Post(
            url = stsUrl,
            body =
                FormDataContent(
                    Parameters.Companion.build {
                        append(
                            GRANT_TYPE,
                            "urn:ietf:params:oauth:grant-type:token-exchange"
                        )
                        append(SUBJECT_TOKEN, accessToken)
                        append(
                            SUBJECT_TOKEN_TYPE,
                            "urn:ietf:params:oauth:token-type:access_token"
                        )
                        append(SCOPE, scope)
                    }
                ),
            headers =
                listOf(
                    Pair("Content-Type", "application/x-www-form-urlencoded")
                )
        )

    /**
     * Handles the response from the service token request - see [attemptServiceTokenExchange].
     * @param response an [ApiResponse] that contains the result of the service token exchange
     * @return [AuthenticationResponse] which returns a response either containing a failure containing a message or
     * a success containing the service token issues
     */
    @Suppress("TooGenericExceptionCaught")
    private fun handleServiceTokenResponse(response: ApiResponse): AuthenticationResponse =
        if (response is ApiResponse.Success<*>) {
            // Attempt to decode the response from json format
            try {
                val tokenResponseString: String = response.response.toString()
                val tokenApiResponse: TokenApiResponse =
                    jsonDecoder
                        .decodeFromString(tokenResponseString)
                AuthenticationResponse.Success(tokenApiResponse.token)
            } catch (e: Exception) {
                // If decoding is unsuccessful log error and return the failure
                val loginException = LoginException(e)
                logger.error(
                    loginException::class.java.simpleName,
                    e.message.toString(),
                    loginException
                )
                AuthenticationResponse.Failure(e)
            }
        } else {
            AuthenticationResponse.Failure(Exception(SERVICE_TOKEN_FAILURE_ERROR_MSG))
        }

    companion object {
        private const val GRANT_TYPE = "grant_type"
        private const val SUBJECT_TOKEN = "subject_token"
        private const val SUBJECT_TOKEN_TYPE = "subject_token_type"
        private const val SCOPE = "scope"

        const val REFRESH_EXCHANGE_ERROR_MSG = "Failed refresh exchange."
        const val MANUAL_SIGN_IN_REQUIRED_ERROR_MSG = "Failed refresh exchange failed - user cannot reauthenticate."
        const val NO_ACCESS_TOKEN_ERROR_MSG = "No access token"
        const val SERVICE_TOKEN_FAILURE_ERROR_MSG = "Failed to fetch service token"
        const val FRAGMENT_ACTIVITY_NULL_ERROR_MSG = "FragmentActivity is null"

        data class FragmentActivityNull(
            val msg: String = FRAGMENT_ACTIVITY_NULL_ERROR_MSG
        ) : Exception(msg)
    }
}
