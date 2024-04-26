package uk.gov.onelogin.login.usecase

import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.login.state.LocalAuthStatus
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.usecases.GetFromSecureStore
import uk.gov.onelogin.tokens.usecases.GetTokenExpiry

interface HandleLogin {
    suspend operator fun invoke(
        fragmentActivity: FragmentActivity,
        callback: (LocalAuthStatus) -> Unit
    )
}

class HandleLoginImpl @Inject constructor(
    private val getTokenExpiry: GetTokenExpiry,
    private val tokenRepository: TokenRepository,
    private val getFromSecureStore: GetFromSecureStore,
    private val bioPrefHandler: BiometricPreferenceHandler
) : HandleLogin {
    override suspend fun invoke(
        fragmentActivity: FragmentActivity,
        callback: (LocalAuthStatus) -> Unit
    ) {
        if (!isTokenExpiredOrMissing() && bioPrefHandler.getBioPref() != BiometricPreference.NONE) {
            getFromSecureStore(fragmentActivity, Keys.ACCESS_TOKENS_KEY) {
                if (it is LocalAuthStatus.Success) {
                    tokenRepository.setTokenResponse(
                        TokenResponse(
                            accessToken = it.accessToken,
                            tokenType = "",
                            accessTokenExpirationTime = getTokenExpiry() ?: 0
                        )
                    )
                }
                callback(it)
            }
        } else {
            callback(LocalAuthStatus.RefreshToken)
        }
    }

    private fun isTokenExpiredOrMissing(): Boolean {
        return getTokenExpiry()?.let { isTimestampExpired(it) } ?: true
    }

    private fun isTimestampExpired(expiryTimestamp: Long) =
        expiryTimestamp < System.currentTimeMillis()
}
