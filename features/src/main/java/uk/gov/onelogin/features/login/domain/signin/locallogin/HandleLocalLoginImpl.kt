package uk.gov.onelogin.features.login.domain.signin.locallogin

import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.tokens.domain.retrieve.GetFromEncryptedSecureStore
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.core.utils.AccessToken
import uk.gov.onelogin.core.utils.RefreshToken

@Suppress("LongParameterList")
class HandleLocalLoginImpl @Inject constructor(
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
    private val localAuthManager: LocalAuthManager
) : HandleLocalLogin {
    override suspend fun invoke(
        fragmentActivity: FragmentActivity,
        callback: (LocalAuthStatus) -> Unit
    ) {
        // Check Local Auth Enabled AND Refresh Token expiry exists
        if (isLocalAuthEnabled() && getRefreshTokenExpiry() != null) {
            // When Refresh Token not expired
            if (!isRefreshTokenExpired()) {
                // Get Refresh and AccessToken (require Access Token to be able to not break existing behaviour since the refresh exchange ahs not been completed yet
                // Cannot get the ID Token because it seems to time out (3 seconds don't seem enough to get all 3)
                getFromEncryptedSecureStore(
                    fragmentActivity,
                    AuthTokenStoreKeys.REFRESH_TOKEN_KEY,
                    AuthTokenStoreKeys.ACCESS_TOKEN_KEY,
                    AuthTokenStoreKeys.ID_TOKEN_KEY
                ) {
                    // When Local Auth Successful
                    if (it is LocalAuthStatus.Success) {
                        // These should never be returned null - secure store checks for all values to not be null
                        val accessToken = it.payload[AuthTokenStoreKeys.ACCESS_TOKEN_KEY]
                        val refreshToken = it.payload[AuthTokenStoreKeys.REFRESH_TOKEN_KEY]
                        val idToken = it.payload[AuthTokenStoreKeys.ID_TOKEN_KEY]
                        if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty() &&
                            !idToken.isNullOrEmpty()
                        ) {
                            tokenRepository.setTokenResponse(
                                TokenResponse(
                                    accessToken = accessToken,
                                    idToken = idToken,
                                    tokenType = "",
                                    accessTokenExpirationTime = getAccessTokenExpiry() ?: 0
                                )
                            )
                        } else {
                            // Finish the function here and break - this handles if one of the tokens is null unexpectedly
                            println(
                                "Refresh token - something went wrong with getting tokens from secure store"
                            )
                            callback(LocalAuthStatus.ManualSignIn)
                            return@getFromEncryptedSecureStore
                        }
                    }
                    // Call the lambda with the result (it doesn't necessarily mean it will be success
                    callback(it)
                }
            } else {
                // If Refresh Token is expired prompt for ReAuth
                callback(LocalAuthStatus.ReAuthSignIn)
            }
        } else {
            // If Refresh Token is null, then use the flow for Access Token only
            handleAccessTokenOnly(fragmentActivity, callback)
        }
    }

    private suspend fun handleAccessTokenOnly(
        fragmentActivity: FragmentActivity,
        callback: (LocalAuthStatus) -> Unit
    ) {
        if (!isAccessTokenExpired() && isLocalAuthEnabled()) {
            getFromEncryptedSecureStore(
                fragmentActivity,
                AuthTokenStoreKeys.ACCESS_TOKEN_KEY,
                AuthTokenStoreKeys.ID_TOKEN_KEY
            ) {
                if (it is LocalAuthStatus.Success) {
                    // These should never be returned null - secure store checks for all values to not be null
                    val accessToken = it.payload[AuthTokenStoreKeys.ACCESS_TOKEN_KEY]
                    val idToken = it.payload[AuthTokenStoreKeys.ID_TOKEN_KEY]
                    if (!accessToken.isNullOrEmpty() && !idToken.isNullOrEmpty()) {
                        tokenRepository.setTokenResponse(
                            TokenResponse(
                                accessToken = accessToken,
                                idToken = idToken,
                                tokenType = "",
                                accessTokenExpirationTime = getAccessTokenExpiry() ?: 0
                            )
                        )
                    } else {
                        callback(LocalAuthStatus.ManualSignIn)
                        return@getFromEncryptedSecureStore
                    }
                }
                callback(it)
            }
        } else {
            if (getAccessTokenExpiry() == null) {
                callback(LocalAuthStatus.ManualSignIn)
            } else {
                callback(LocalAuthStatus.ReAuthSignIn)
            }
        }
    }

    private fun isLocalAuthEnabled(): Boolean {
        val prefs = localAuthManager.localAuthPreference
        return !(prefs == LocalAuthPreference.Disabled || prefs == null)
    }
}
