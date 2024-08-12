package uk.gov.onelogin.login.usecase

import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.login.state.LocalAuthStatus
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.usecases.GetFromTokenSecureStore
import uk.gov.onelogin.tokens.usecases.GetTokenExpiry

fun interface HandleLogin {
    suspend operator fun invoke(
        fragmentActivity: FragmentActivity,
        callback: (LocalAuthStatus) -> Unit
    )
}

class HandleLoginImpl @Inject constructor(
    private val getTokenExpiry: GetTokenExpiry,
    private val tokenRepository: TokenRepository,
    private val getFromTokenSecureStore: GetFromTokenSecureStore,
    private val bioPrefHandler: BiometricPreferenceHandler
) : HandleLogin {
    override suspend fun invoke(
        fragmentActivity: FragmentActivity,
        callback: (LocalAuthStatus) -> Unit
    ) {
        if (!isTokenExpiredOrMissing() && bioPrefHandler.getBioPref() != BiometricPreference.NONE) {
            getFromTokenSecureStore(fragmentActivity, Keys.ACCESS_TOKEN_KEY) {
                if (it is LocalAuthStatus.Success) {
                    tokenRepository.setTokenResponse(
                        TokenResponse(
                            accessToken = it.payload,
                            tokenType = "",
                            accessTokenExpirationTime = getTokenExpiry() ?: 0
                        )
                    )
                }
                callback(it)
            }
        } else {
            callback(LocalAuthStatus.ManualSignIn)
        }
    }

    private fun isTokenExpiredOrMissing(): Boolean {
        return getTokenExpiry()?.let { isTimestampExpired(it) } ?: true
    }

    private fun isTimestampExpired(expiryTimestamp: Long) =
        expiryTimestamp < System.currentTimeMillis()
}
