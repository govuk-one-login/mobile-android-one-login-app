package uk.gov.onelogin.login.usecase

import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.usecases.GetFromSecureStore
import uk.gov.onelogin.tokens.usecases.GetTokenExpiry

interface HandleLogin {
    suspend operator fun invoke(fragmentActivity: FragmentActivity): Boolean
}

class HandleLoginImpl @Inject constructor(
    private val getTokenExpiry: GetTokenExpiry,
    private val tokenRepository: TokenRepository,
    private val getFromSecureStore: GetFromSecureStore
) : HandleLogin {
    override suspend fun invoke(fragmentActivity: FragmentActivity): Boolean {
        return if (!isTokenExpiredOrMissing()) {
            handleSecureStoreBiometricAccess(fragmentActivity)
        } else {
            false
        }
    }

    private fun isTokenExpiredOrMissing(): Boolean {
        return getTokenExpiry()?.let { isTimestampExpired(it) } ?: true
    }

    private fun isTimestampExpired(expiryTimestamp: Long) =
        expiryTimestamp < System.currentTimeMillis()

    private suspend fun handleSecureStoreBiometricAccess(
        fragmentActivity: FragmentActivity
    ): Boolean {
        val accessToken: String? =
            getFromSecureStore(context = fragmentActivity, key = Keys.ACCESS_TOKENS_KEY)
        return when {
            accessToken.isNullOrBlank() -> false
            else -> {
                tokenRepository.setTokenResponse(
                    TokenResponse(
                        accessToken = accessToken,
                        tokenType = "",
                        accessTokenExpirationTime = getTokenExpiry() ?: 0
                    )
                )
                true
            }
        }
    }
}
