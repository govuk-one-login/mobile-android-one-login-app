package uk.gov.onelogin.features.login.domain.refresh

import android.content.Context
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.Parameters
import uk.gov.android.network.api.v2.ApiRequest
import uk.gov.android.network.api.v3.ApiResponse
import uk.gov.android.network.service.ClientAttestationException
import uk.gov.android.network.service.NetworkingException
import uk.gov.android.network.service.v2.NetworkService
import uk.gov.android.network.service.v2.NetworkServiceTypedSuccessExt.makeRequest
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.tokens.RefreshExchangeApiResponse
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.tokendata.LoginTokens
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
import uk.gov.onelogin.features.login.domain.validateWalletStoreId.ValidateWalletStoreId
import java.time.Instant
import javax.inject.Inject
import kotlin.text.isNullOrEmpty

@Suppress("LongParameterList")
class RefreshExchangeImpl
    @Inject
    constructor(
        @param:ApplicationContext
        private val context: Context,
        private val getPersistentId: GetPersistentId,
        @param:RefreshToken
        private val isRefreshTokenExpired: IsTokenExpired,
        private val networkService: NetworkService,
        private val getFromEncryptedSecureStore: GetFromEncryptedSecureStore,
        private val saveTokenExpiry: SaveTokenExpiry,
        private val tokenRepository: TokenRepository,
        private val saveTokens: SaveTokens,
        private val logger: Logger,
        private val timeProvider: TimeProvider,
        private val validateWalletStoreId: ValidateWalletStoreId,
    ) : RefreshExchange {
        private var refreshToken = ""
        private var idToken = ""
        private var areChecksSuccessful = false

        override suspend fun getTokens(
            context: FragmentActivity,
            handleResult: (RefreshExchangeResult) -> Unit,
        ) {
            // Check the persistent session ID is valid
            if (!getPersistentId().isNullOrEmpty()) {
                // Check Refresh token is NOT expired and wallet store ID is valid
                if (!isRefreshTokenExpired() && validateWalletStoreId()) {
                    getTokensFromSecureStore(context, handleResult)
                } else {
                    // When Refresh token is invalid or wallet store ID is missing
                    handleResult(RefreshExchangeResult.ReauthRequired)
                    return
                }
            } else {
                // When a persistent session ID couldn't be retrieved or is invalid
                handleResult(RefreshExchangeResult.FirstTimeUser)

                return
            }
            // This will handle the refresh token call and return of the updated tokens
            // Will only be called if the checks were successful, otherwise, we use return to break the loops (see above)
            if (areChecksSuccessful) handleResult(makeRefreshTokenCall())
        }

        private suspend fun getTokensFromSecureStore(
            context: FragmentActivity,
            onFailure: (RefreshExchangeResult) -> Unit,
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
                            it.payload?.get(AuthTokenStoreKeys.REFRESH_TOKEN_KEY)
                        val idToken = it.payload?.get(AuthTokenStoreKeys.ID_TOKEN_KEY)
                        // Check is valid (should never be returned null)
                        if (!refreshToken.isNullOrEmpty() && !idToken.isNullOrEmpty()) {
                            // Set all required values to enable the refresh request to be made/ continue
                            this@RefreshExchangeImpl.refreshToken = refreshToken
                            this@RefreshExchangeImpl.idToken = idToken
                            areChecksSuccessful = true
                        } else {
                            // When Refresh token is invalid then prompt user to re-auth as the refresh token won't be able to be exchanged for a new one
                            onFailure(RefreshExchangeResult.ReauthRequired)
                        }
                    } else {
                        // If the retrieval failed
                        // Call lambda to handle the result from the consumer/ call point based on the LocalAuthStatus passed in
                        val result = mapLocalAuthStatusToRefreshExchangeResult(it)
                        onFailure(result)
                    }
                },
            )
        }

        @Suppress("TooGenericExceptionCaught")
        private suspend fun makeRefreshTokenCall(): RefreshExchangeResult {
            val refreshExchangeResult =
                // Attempt to exchange existing refresh token for new tokens returned in a TokenResponse format
                retrieveNewTokens(refreshToken = refreshToken)
            return when (refreshExchangeResult) {
                is ApiResponse.Success -> {
                    val tokens = refreshExchangeResult.body
                    // Save new access and refresh tokens expiry
                    saveTokensExpiryToOpenStore(tokens)
                    val tokenResponse =
                        LoginTokens(
                            tokenType = tokens.tokenType,
                            accessToken = tokens.accessToken,
                            accessTokenExpirationTime =
                                timeProvider.calculateExpiryTime(tokens.expiresIn),
                            idToken = this.idToken,
                        )
                    // Update Token Repository (memory)
                    tokenRepository.setTokenResponse(tokenResponse)
                    // Update access and refresh token in secure store
                    saveTokens.save(tokens.refreshToken)
                    RefreshExchangeResult.Success
                }
                is ApiResponse.Failure -> {
                    logger.error(
                        REFRESH_ERROR_TAG,
                        refreshExchangeResult.error.message,
                        refreshExchangeResult.error,
                    )
                    when (refreshExchangeResult.error) {
                        is ClientAttestationException -> RefreshExchangeResult.ClientAttestationFailure
                        else -> RefreshExchangeResult.ReauthRequired
                    }
                }
            }
        }

        suspend fun retrieveNewTokens(
            refreshToken: String,
        ): ApiResponse<RefreshExchangeApiResponse, String, NetworkingException> {
            // CGet the required URL
            val authUrl =
                context.getString(
                    R.string.stsUrl,
                    context.getString(R.string.tokenExchangeEndpoint),
                )
            val request =
                createApiRequestPost(
                    authUrl = authUrl,
                    refreshToken = refreshToken,
                )

            return networkService.makeRequest<RefreshExchangeApiResponse>(
                apiRequest = request,
            ) {
                withAttestation = true
                withRefreshDPoP = true
            }
        }

        private fun createApiRequestPost(
            authUrl: String,
            refreshToken: String,
        ): ApiRequest =
            ApiRequest.Post(
                url = authUrl,
                body =
                    FormDataContent(
                        Parameters.build {
                            append(
                                GRANT_TYPE_LABEL,
                                GRANT_TYPE_VALUE,
                            )
                            append(
                                REFRESH_TOKEN_LABEL,
                                refreshToken,
                            )
                        },
                    ),
                headers =
                    listOf(
                        CONTENT_TYPE_LABEL to CONTENT_TYPE_VALUE,
                    ),
            )

        private suspend fun saveTokensExpiryToOpenStore(tokens: RefreshExchangeApiResponse) {
            // Calculate the correct expiry
            val accessTokenExp =
                Instant.now().toEpochMilli() +
                    Instant.ofEpochSecond(tokens.expiresIn).toEpochMilli()
            // Save access token exp
            saveTokenExpiry.saveExp(
                ExpiryInfo(
                    key = ACCESS_TOKEN_EXPIRY_KEY,
                    value = accessTokenExp,
                ),
            )
            // Save refresh token exp
            tokens.refreshToken?.let {
                val extractedExp = saveTokenExpiry.extractExpFromRefreshToken(it)
                saveTokenExpiry.saveExp(
                    ExpiryInfo(
                        key = REFRESH_TOKEN_EXPIRY_KEY,
                        value = extractedExp,
                    ),
                )
            }
        }

        private fun mapLocalAuthStatusToRefreshExchangeResult(status: LocalAuthStatus): RefreshExchangeResult {
            // This will have to updated after the secure store errors are now mapped correctly according to the TD
            return when (status) {
                is LocalAuthStatus.FirstTimeUser -> RefreshExchangeResult.FirstTimeUser
                is LocalAuthStatus.UnrecoverableError -> RefreshExchangeResult.UnrecoverableError
                is LocalAuthStatus.Success -> RefreshExchangeResult.Success
                is LocalAuthStatus.UserCancelledBioPrompt -> RefreshExchangeResult.UserCancelledBioPrompt
                else -> RefreshExchangeResult.ReauthRequired
            }
        }

        companion object {
            const val REFRESH_ERROR_TAG = "Refresh Exchange Tokens Error"
            private const val CONTENT_TYPE_LABEL = "Content-Type"
            private const val CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded"
            private const val GRANT_TYPE_LABEL = "grant_type"
            private const val GRANT_TYPE_VALUE = "refresh_token"
            private const val REFRESH_TOKEN_LABEL = "refresh_token"

            data class RefreshExchangeException(
                private val msg: String,
            ) : Exception(msg)
        }
    }
