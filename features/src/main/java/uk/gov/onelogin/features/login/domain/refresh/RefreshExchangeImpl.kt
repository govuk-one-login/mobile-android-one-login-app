package uk.gov.onelogin.features.login.domain.refresh

import android.content.Context
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.Parameters
import java.time.Instant
import javax.inject.Inject
import kotlin.text.isNullOrEmpty
import kotlinx.serialization.json.Json
import uk.gov.android.authentication.integrity.pop.SignedPoP
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.authentication.login.refresh.DemonstratingProofOfPossessionManager
import uk.gov.android.authentication.login.refresh.SignedDPoP
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.RefreshExchangeApiResponse
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.tokens.domain.retrieve.GetFromEncryptedSecureStore
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.ExpiryInfo
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.utils.RefreshToken
import uk.gov.onelogin.core.utils.TimeProvider
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationResult

@Suppress("LongParameterList")
class RefreshExchangeImpl @Inject constructor(
    @param:ApplicationContext
    private val context: Context,
    private val getPersistentId: GetPersistentId,
    @param:RefreshToken
    private val isRefreshTokenExpired: IsTokenExpired,
    private val httpClient: GenericHttpClient,
    private val appIntegrity: AppIntegrity,
    private val dPoPManager: DemonstratingProofOfPossessionManager,
    private val getFromEncryptedSecureStore: GetFromEncryptedSecureStore,
    private val saveTokenExpiry: SaveTokenExpiry,
    private val tokenRepository: TokenRepository,
    private val saveTokens: SaveTokens,
    private val logger: Logger,
    private val timeProvider: TimeProvider
) : RefreshExchange {
    private val jsonDecoder = Json { ignoreUnknownKeys = true }

    // Initialise token and client attestation field
    private var refreshToken = ""
    private var idToken = ""
    private var clientAttestation: String = ""
    private var areChecksSuccessful = false

    override suspend fun getTokens(
        context: FragmentActivity,
        handleResult: (LocalAuthStatus) -> Unit
    ) {
        // Check the persistent session ID is valid
        if (!getPersistentId().isNullOrEmpty()) {
            // Check Refresh token is NOT expired
            if (!isRefreshTokenExpired()) {
                // Attempt to get Client Attestation
                getClientAttestationAndRetrieveTokensFromSecureStore(context, handleResult)
            } else {
                // When Refresh token is invalid - prompt for re-auth to be able to get a v new Refresh token
                // Call lambda to handle the result from the consumer/ call point based on the LocalAuthStatus passed in
                handleResult(LocalAuthStatus.ReAuthSignIn)
                return
            }
        } else {
            // When a persistent session ID couldn't be retrieved or is invalid
            // Call lambda to handle the result from the consumer/ call point based on the LocalAuthStatus passed in
            handleResult(LocalAuthStatus.ManualSignIn)
            return
        }
        // This will handle the refresh token call and return of the updated tokens
        // Will only be called if the checks were successful, otherwise, we use return to break the loops (see above)
        if (areChecksSuccessful) makeRefreshTokenCall(handleResult)
    }

    override suspend fun getClientAttestationAndRetrieveTokensFromSecureStore(
        context: FragmentActivity,
        handleResult: (LocalAuthStatus) -> Unit
    ) {
        when (val attestation = appIntegrity.getClientAttestation()) {
            // If attestation exists and is valid OR if required, a new one is successfully retrieved
            is AttestationResult.Success -> {
                // Attempt to retrieve the Refresh token from the secure store
                handleRefreshTokenRetrieval(
                    context = context,
                    onSuccess = { refreshToken, idToken ->
                        // Update the local fields for the required parameters
                        this@RefreshExchangeImpl.refreshToken = refreshToken
                        this@RefreshExchangeImpl.idToken = idToken
                        clientAttestation = attestation.clientAttestation
                        areChecksSuccessful = true
                    },
                    onFailure = {
                        handleResult(it)
                        areChecksSuccessful = false
                    }
                )
            }
            is AttestationResult.NotRequired -> {
                // Attempt to retrieve the Refresh token from the secure store
                handleRefreshTokenRetrieval(
                    context = context,
                    onSuccess = { refreshToken, idToken ->
                        this@RefreshExchangeImpl.refreshToken = refreshToken
                        this@RefreshExchangeImpl.idToken = idToken
                        clientAttestation = attestation.savedAttestation ?: ""
                        areChecksSuccessful = true
                    },
                    onFailure = {
                        handleResult(it)
                        areChecksSuccessful = false
                    }
                )
            }
            // For any errors returned from the attempt to get a new client attestation/ retrieve existing one
            // Call lambda to handle the result fromm the consumer/ call point based on the LocalAuthStatus passed in
            else -> {
                areChecksSuccessful = false
                handleResult(LocalAuthStatus.ClientAttestationFailure)
            }
        }
    }

    private suspend fun handleRefreshTokenRetrieval(
        context: FragmentActivity,
        onSuccess: (refreshToken: String, idToken: String) -> Unit,
        onFailure: (LocalAuthStatus) -> Unit
    ) {
        // Attempt to retrieve the Refresh token from the secure store
        getFromEncryptedSecureStore(
            context = context,
            AuthTokenStoreKeys.REFRESH_TOKEN_KEY,
            AuthTokenStoreKeys.ID_TOKEN_KEY,
            callback = {
                // When the local auth has been successfully completed
                if (it is LocalAuthStatus.Success) {
                    val refreshToken =
                        it.payload[AuthTokenStoreKeys.REFRESH_TOKEN_KEY]
                    val idToken = it.payload[AuthTokenStoreKeys.ID_TOKEN_KEY]
                    // Check is valid (should never be returned null)
                    if (!refreshToken.isNullOrEmpty() && !idToken.isNullOrEmpty()) {
                        // Call lambda to handle the result from the consumer/ call point based on the LocalAuthStatus passed in
                        onSuccess(refreshToken, idToken)
                    } else {
                        // When Refresh token is invalid then prompt user to re-auth as the refresh token won't be able to be exchanged for a new one
                        onFailure(LocalAuthStatus.ReAuthSignIn)
                    }
                } else {
                    // If the retrieval failed
                    // Call lambda to handle the result from the consumer/ call point based on the LocalAuthStatus passed in
                    onFailure(it)
                }
            }
        )
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun makeRefreshTokenCall(
        handleResult: (LocalAuthStatus) -> Unit
    ) {
        val refreshExchangeResult = try {
            // Attempt to exchange existing refresh token for new tokens returned in a TokenResponse format
            retrieveNewTokens(refreshToken = refreshToken, clientAttestation = clientAttestation)
        } catch (e: Exception) {
            // Log error
            logger.error(
                REFRESH_ERROR_TAG,
                e.message ?: EMPTY_MSG,
                e
            )
            // All errors are to be directed to re-auth (as of 10/11/25)
            handleResult(LocalAuthStatus.ReAuthSignIn)
            return
        }
        when (refreshExchangeResult) {
            is ApiResponse.Success<*> -> {
                // Decode tokens from response
                val decodedTokens =
                    jsonDecoder.decodeFromString<RefreshExchangeApiResponse>(
                        refreshExchangeResult.response.toString()
                    )
                // Save new access and refresh tokens expiry
                saveTokensExpiryToOpenStore(decodedTokens)
                val tokenResponse = TokenResponse(
                    tokenType = decodedTokens.tokenType,
                    accessToken = decodedTokens.accessToken,
                    accessTokenExpirationTime =
                    timeProvider.calculateExpiryTime(decodedTokens.expiresIn),
                    idToken = this.idToken,
                    refreshToken = ""
                )
                // Update Token Repository (memory)
                tokenRepository.setTokenResponse(tokenResponse)
                // Update access and refresh token in secure store
                saveTokens.save(decodedTokens.refreshToken)
                // Call lambda to exit the function with a LocalAuthStatus.Success
                // To be determined when implementing if it requires the refresh token to be passed to the consumer
                handleResult(LocalAuthStatus.Success(mapOf()))
            }
            is ApiResponse.Failure -> {
                logger.error(
                    REFRESH_ERROR_TAG,
                    refreshExchangeResult.error.message ?: EMPTY_MSG,
                    refreshExchangeResult.error
                )
                handleResult(LocalAuthStatus.ReAuthSignIn)
            }
            else -> {
                handleResult(LocalAuthStatus.ReAuthSignIn)
            }
        }
    }

    suspend fun retrieveNewTokens(
        refreshToken: String,
        clientAttestation: String
    ): ApiResponse {
        // CGet the required URL
        val authUrl = context.getString(
            R.string.stsUrl,
            context.getString(R.string.tokenExchangeEndpoint)
        )
        // Attempt to generate DPoP
        val dPoP = dPoPManager.generateDPoP(authUrl)
        // Attempt to generate PoP
        val pop = appIntegrity.getProofOfPossession()
        // Throw error if DPoP couldn't be generated
        when (dPoP) {
            is SignedDPoP.Success -> {
                when (pop) {
                    is SignedPoP.Success -> {
                        // If all successful - continue to make the tokens exchange request
                        val request = createApiRequestPost(
                            authUrl = authUrl,
                            refreshToken = refreshToken,
                            clientAttestation = clientAttestation,
                            dPoP = dPoP,
                            pop = pop
                        )

                        return httpClient.makeRequest(
                            apiRequest = request
                        )
                    }
                    is SignedPoP.Failure -> {
                        val fallbackExp = RefreshExchangeException(ATTESTATION_POP_GENERATE_ERROR)
                        logger.error(
                            REFRESH_ERROR_TAG,
                            pop.reason,
                            pop.error ?: fallbackExp
                        )
                        // Throw error if Client Attestation PoP couldn't be generated
                        throw fallbackExp
                    }
                }
            }

            is SignedDPoP.Failure -> {
                val fallbackExp = RefreshExchangeException(DPOP_GENERATE_ERROR)
                logger.error(
                    REFRESH_ERROR_TAG,
                    dPoP.reason,
                    dPoP.error ?: fallbackExp
                )
                throw RefreshExchangeException(DPOP_GENERATE_ERROR)
            }
        }
    }

    private fun createApiRequestPost(
        authUrl: String,
        refreshToken: String,
        clientAttestation: String,
        dPoP: SignedDPoP.Success,
        pop: SignedPoP.Success
    ): ApiRequest {
        return ApiRequest.Post(
            url = authUrl,
            body = FormDataContent(
                Parameters.build {
                    append(
                        GRANT_TYPE_LABEL,
                        GRANT_TYPE_VALUE
                    )
                    append(
                        REFRESH_TOKEN_LABEL,
                        refreshToken
                    )
                }
            ),
            headers = listOf(
                CONTENT_TYPE_LABEL to CONTENT_TYPE_VALUE,
                DPOP_HEADER_LABEL to dPoP.popJwt,
                CLIENT_ATTESTATION_HEADER_LABEL to clientAttestation,
                POP_CLIENT_ATTESTATION_HEADER_LABEL to pop.popJwt
            )
        )
    }

    private fun saveTokensExpiryToOpenStore(tokens: RefreshExchangeApiResponse) {
        // Calculate the correct expiry
        val accessTokenExp = Instant.now().toEpochMilli() +
            Instant.ofEpochSecond(tokens.expiresIn).toEpochMilli()
        // Save access token exp
        saveTokenExpiry.saveExp(
            ExpiryInfo(
                key = ACCESS_TOKEN_EXPIRY_KEY,
                value = accessTokenExp
            )
        )
        // Save refresh token exp
        tokens.refreshToken?.let {
            val extractedExp = saveTokenExpiry.extractExpFromRefreshToken(it)
            saveTokenExpiry.saveExp(
                ExpiryInfo(
                    key = REFRESH_TOKEN_EXPIRY_KEY,
                    value = extractedExp
                )
            )
        }
    }

    companion object {
        const val REFRESH_ERROR_TAG = "Refresh Exchange Tokens Error"
        const val EMPTY_MSG = "Refresh Token Exchange Error - Error message was null."
        const val DPOP_GENERATE_ERROR = "Couldn't generate DPoP."
        const val ATTESTATION_POP_GENERATE_ERROR = "Couldn't generate App Integrity PoP."
        private const val CONTENT_TYPE_LABEL = "Content-Type"
        private const val CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded"
        private const val GRANT_TYPE_LABEL = "grant_type"
        private const val GRANT_TYPE_VALUE = "refresh_token"
        private const val REFRESH_TOKEN_LABEL = "refresh_token"
        private const val DPOP_HEADER_LABEL = "DPop"
        private const val CLIENT_ATTESTATION_HEADER_LABEL = "OAuth-Client-Attestation"
        private const val POP_CLIENT_ATTESTATION_HEADER_LABEL = "OAuth-Client-Attestation-PoP"

        data class RefreshExchangeException(private val msg: String) : Exception(msg)
    }
}
