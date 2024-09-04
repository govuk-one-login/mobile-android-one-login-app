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
            getFromTokenSecureStore(
                fragmentActivity,
                Keys.ACCESS_TOKEN_KEY,
                Keys.ID_TOKEN_KEY
            ) {
                if (it is LocalAuthStatus.Success) {
                    it.payload[Keys.ACCESS_TOKEN_KEY]?.let { accessToken ->
                        tokenRepository.setTokenResponse(
                            TokenResponse(
                                accessToken = accessToken,
                                tokenType = "",
                                accessTokenExpirationTime = getTokenExpiry() ?: 0,
                                idToken = it.payload[Keys.ID_TOKEN_KEY]
                            )
                        )
                        callback(it)
                    } ?: callback(LocalAuthStatus.SecureStoreError)
                } else {
                    callback(it)
                }
            }
        } else {
            callback(LocalAuthStatus.ManualSignIn)
        }
    }

    private fun isTokenExpiredOrMissing() =
        getTokenExpiry()?.let { isTimestampExpired(it) } ?: true

    private fun isTimestampExpired(expiryTimestamp: Long) =
        expiryTimestamp < System.currentTimeMillis()
}
