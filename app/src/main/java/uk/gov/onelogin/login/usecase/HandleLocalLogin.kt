package uk.gov.onelogin.login.usecase

import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.login.state.LocalAuthStatus
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.usecases.GetFromTokenSecureStore
import uk.gov.onelogin.tokens.usecases.GetTokenExpiry
import uk.gov.onelogin.tokens.usecases.IsAccessTokenExpired

fun interface HandleLocalLogin {
    suspend operator fun invoke(
        fragmentActivity: FragmentActivity,
        callback: (LocalAuthStatus) -> Unit
    )
}

class HandleLocalLoginImpl @Inject constructor(
    private val getTokenExpiry: GetTokenExpiry,
    private val tokenRepository: TokenRepository,
    private val isAccessTokenExpired: IsAccessTokenExpired,
    private val getFromTokenSecureStore: GetFromTokenSecureStore,
    private val bioPrefHandler: BiometricPreferenceHandler
) : HandleLocalLogin {
    override suspend fun invoke(
        fragmentActivity: FragmentActivity,
        callback: (LocalAuthStatus) -> Unit
    ) {
        if (!isAccessTokenExpired() && bioPrefHandler.getBioPref() != BiometricPreference.NONE) {
            getFromTokenSecureStore(fragmentActivity, Keys.ACCESS_TOKEN_KEY, Keys.ID_TOKEN_KEY) {
                if (it is LocalAuthStatus.Success) {
                    // These should never be returned null - secure store checks for all values to not be null
                    val accessToken = it.payload[Keys.ACCESS_TOKEN_KEY]
                    val idToken = it.payload[Keys.ID_TOKEN_KEY]
                    if (!accessToken.isNullOrEmpty() && !idToken.isNullOrEmpty()) {
                        tokenRepository.setTokenResponse(
                            TokenResponse(
                                accessToken = accessToken,
                                idToken = idToken,
                                tokenType = "",
                                accessTokenExpirationTime = getTokenExpiry() ?: 0
                            )
                        )
                    } else {
                        callback(LocalAuthStatus.ManualSignIn)
                    }
                }
                callback(it)
            }
        } else {
            if (getTokenExpiry() == null) {
                callback(LocalAuthStatus.ManualSignIn)
            } else {
                callback(LocalAuthStatus.ReAuthSignIn)
            }
        }
    }
}
