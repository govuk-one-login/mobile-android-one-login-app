package uk.gov.onelogin.features.login.domain.signin.locallogin

import androidx.fragment.app.FragmentActivity
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.logging.api.v2.Logger
import uk.gov.logging.api.v2.errorKeys.ErrorKeys
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.tokendata.LoginTokens
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.tokens.domain.retrieve.GetFromEncryptedSecureStore
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.core.utils.AccessToken
import uk.gov.onelogin.core.utils.RefreshToken
import uk.gov.onelogin.features.wallet.domain.WalletIsEmptyUseCase
import uk.gov.onelogin.features.wallet.domain.WalletIsEmptyUseCaseImpl
import javax.inject.Inject

@Suppress("LongParameterList", "TooGenericExceptionCaught")
class HandleLocalLoginImpl
    @Inject
    constructor(
        @param:AccessToken
        private val getAccessTokenExpiry: GetTokenExpiry,
        @param:RefreshToken
        private val getRefreshTokenExpiry: GetTokenExpiry,
        private val tokenRepository: TokenRepository,
        @param:AccessToken
        private val isAccessTokenExpired: IsTokenExpired,
        @param:RefreshToken
        private val isRefreshTokenExpired: IsTokenExpired,
        private val getFromEncryptedSecureStore: GetFromEncryptedSecureStore,
        private val localAuthManager: LocalAuthManager,
        private val getPersistentId: GetPersistentId,
        private val walletIsEmptyUseCase: WalletIsEmptyUseCase,
        private val logger: Logger
    ) : HandleLocalLogin {
        override suspend fun invoke(
            fragmentActivity: FragmentActivity,
            callback: (LocalAuthStatus) -> Unit,
        ) {
            try {
                if (getPersistentId().isNullOrEmpty()) {
                    callback(LocalAuthStatus.FirstTimeUser)
                    val isWalletEmpty = walletIsEmptyUseCase.invoke()
                    if (!isWalletEmpty) {
                        val exp = WalletIsEmptyUseCaseImpl.WalletIsEmptyDataError()
                        logError(exp, exp.message)
                    }
                } else {
                    // Check Local Auth Enabled AND Refresh Token expiry exists
                    if (isLocalAuthEnabled() && getRefreshTokenExpiry() != null) {
                        handleRefreshToken(fragmentActivity, callback)
                    } else {
                        // If Refresh Token is null, then use the flow for Access Token only
                        handleAccessTokenOnly(fragmentActivity, callback)
                    }
                }
            } catch (e: Throwable) {
                val reason = "secure wallet data deleted"
                logError(e, reason)
            } catch (walletError: WalletIsEmptyUseCaseImpl.CouldNotDetermineIfWalletIsEmpty) {
                val reason = walletError.message ?: "could not determine if wallet is empty"
                logError(walletError, reason)
            }
        }

        private suspend fun handleRefreshToken(
            fragmentActivity: FragmentActivity,
            callback: (LocalAuthStatus) -> Unit,
        ) {
            // When Refresh Token not expired
            if (!isRefreshTokenExpired()) {
                // Get Refresh and AccessToken (require Access Token to be able to not break existing behaviour since the refresh exchange ahs not been completed yet
                // Cannot get the ID Token because it seems to time out (3 seconds don't seem enough to get all 3)
                val expiryTime = getAccessTokenExpiry() ?: 0
                getFromEncryptedSecureStore(
                    fragmentActivity,
                    AuthTokenStoreKeys.REFRESH_TOKEN_KEY,
                    AuthTokenStoreKeys.ACCESS_TOKEN_KEY,
                    AuthTokenStoreKeys.ID_TOKEN_KEY,
                ) {
                    // When Local Auth Successful
                    if (it is LocalAuthStatus.Success) {
                        // These should never be returned null - secure store checks for all values to not be null
                        val accessToken = it.payload?.get(AuthTokenStoreKeys.ACCESS_TOKEN_KEY)
                        val refreshToken = it.payload?.get(AuthTokenStoreKeys.REFRESH_TOKEN_KEY)
                        val idToken = it.payload?.get(AuthTokenStoreKeys.ID_TOKEN_KEY)
                        if (!accessToken.isNullOrEmpty() &&
                            !refreshToken.isNullOrEmpty() &&
                            !idToken.isNullOrEmpty()
                        ) {
                            tokenRepository.setTokenResponse(
                                LoginTokens(
                                    accessToken = accessToken,
                                    idToken = idToken,
                                    tokenType = "",
                                    accessTokenExpirationTime = expiryTime,
                                ),
                            )
                        } else {
                            // Finish the function here and break - this handles if one of the tokens is null unexpectedly
                            callback(LocalAuthStatus.ReauthRequired)
                            return@getFromEncryptedSecureStore
                        }
                    }
                    // Call the lambda with the result (it doesn't necessarily mean it will be success
                    callback(it)
                }
            } else {
                // If Refresh Token is expired prompt for ReAuth
                callback(LocalAuthStatus.ReauthRequired)
            }
        }

        @Suppress("NestedBlockDepth")
        private suspend fun handleAccessTokenOnly(
            fragmentActivity: FragmentActivity,
            callback: (LocalAuthStatus) -> Unit,
        ) {
            if (!isAccessTokenExpired() && isLocalAuthEnabled()) {
                val expiryTime = getAccessTokenExpiry() ?: 0
                getFromEncryptedSecureStore(
                    fragmentActivity,
                    AuthTokenStoreKeys.ACCESS_TOKEN_KEY,
                    AuthTokenStoreKeys.ID_TOKEN_KEY,
                ) {
                    if (it is LocalAuthStatus.Success) {
                        // These should never be returned null - secure store checks for all values to not be null
                        val accessToken = it.payload?.get(AuthTokenStoreKeys.ACCESS_TOKEN_KEY)
                        val idToken = it.payload?.get(AuthTokenStoreKeys.ID_TOKEN_KEY)
                        if (!accessToken.isNullOrEmpty() && !idToken.isNullOrEmpty()) {
                            tokenRepository.setTokenResponse(
                                LoginTokens(
                                    accessToken = accessToken,
                                    idToken = idToken,
                                    tokenType = "",
                                    accessTokenExpirationTime = expiryTime,
                                ),
                            )
                        } else {
                            callback(LocalAuthStatus.ReauthRequired)
                            return@getFromEncryptedSecureStore
                        }
                    }
                    callback(it)
                }
            } else {
                if (getAccessTokenExpiry() == null) {
                    callback(LocalAuthStatus.FirstTimeUser)
                    try {
                        val isWalletEmpty = walletIsEmptyUseCase.invoke()
                        if (!isWalletEmpty) {
                            val exp = WalletIsEmptyUseCaseImpl.WalletIsEmptyDataError()
                            logError(exp, exp.message)
                        }
                    } catch (walletError: WalletIsEmptyUseCaseImpl.CouldNotDetermineIfWalletIsEmpty) {
                        val reason = walletError.message ?: "could not determine if wallet is empty"
                        logError(walletError, reason)
                    }
                } else {
                    callback(LocalAuthStatus.ReauthRequired)
                }
            }
        }

        private fun isLocalAuthEnabled(): Boolean {
            val prefs = localAuthManager.localAuthPreference
            return !(prefs == LocalAuthPreference.Disabled || prefs == null)
        }

        private fun logError(
            e: Throwable,
            reason: String
        ) {
            logger.error(
                this.javaClass.simpleName,
                e.message ?: "error",
                e,
                ErrorKeys.StringKey("reason", reason)
            )
        }
    }
