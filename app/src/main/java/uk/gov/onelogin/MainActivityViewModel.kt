package uk.gov.onelogin

import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.credentialchecker.BiometricStatus.SUCCESS
import uk.gov.onelogin.credentialchecker.CredentialChecker
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.usecases.GetFromSecureStore
import uk.gov.onelogin.tokens.usecases.GetTokenExpiry
import uk.gov.onelogin.ui.error.ErrorRoutes
import uk.gov.onelogin.ui.home.HomeRoutes

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val appRoutes: IAppRoutes,
    private val loginSession: LoginSession,
    private val credChecker: CredentialChecker,
    private val bioPrefHandler: BiometricPreferenceHandler,
    private val getTokenExpiry: GetTokenExpiry,
    private val getFromSecureStore: GetFromSecureStore,
    private val tokenRepository: TokenRepository
) : ViewModel() {
    private val tag = this::class.java.simpleName

    private val _next = MutableLiveData<String>()
    val next: LiveData<String> = _next
    var keepSplashScreenOn = true

    fun handleIntent(
        intent: Intent?,
        fragmentActivity: FragmentActivity
    ) {
        val data = intent?.data
        when {
            data != null -> handleActivityResult(intent)
            isTokenExpiredOrMissing() -> {
                _next.value = LoginRoutes.START
                keepSplashScreenOn = false
            }

            else -> handleSecureStoreBiometricAccess(fragmentActivity)
        }
    }

    private fun handleSecureStoreBiometricAccess(fragmentActivity: FragmentActivity) {
        viewModelScope.launch {
            val accessToken: String? =
                getFromSecureStore(context = fragmentActivity, key = Keys.ACCESS_TOKENS_KEY)
            when {
                accessToken.isNullOrBlank() -> _next.value = LoginRoutes.START
                else -> {
                    tokenRepository.setTokenResponse(
                        TokenResponse(
                            accessToken = accessToken,
                            tokenType = "",
                            accessTokenExpirationTime = getTokenExpiry() ?: 0
                        )
                    )
                    _next.value = HomeRoutes.START
                }
            }
            keepSplashScreenOn = false
        }
    }

    private fun isTokenExpiredOrMissing(): Boolean {
        return getTokenExpiry()?.let { isTimestampExpired(it) } ?: true
    }

    private fun isTimestampExpired(expiryTimestamp: Long) =
        expiryTimestamp < System.currentTimeMillis()

    @Suppress("TooGenericExceptionCaught")
    private fun handleActivityResult(intent: Intent) {
        try {
            loginSession.finalise(intent = intent) { tokens ->
                tokenRepository.setTokenResponse(tokens)

                if (!credChecker.isDeviceSecure()) {
                    bioPrefHandler.setBioPref(BiometricPreference.NONE)
                    _next.value = LoginRoutes.PASSCODE_INFO
                } else if (shouldSeeBiometricOptIn()) {
                    _next.value = LoginRoutes.BIO_OPT_IN
                } else {
                    _next.value = HomeRoutes.START
                }
            }
        } catch (e: Throwable) { // handle both Error and Exception types
            Log.e(tag, e.message, e)
            _next.value = ErrorRoutes.ROOT
        }
        keepSplashScreenOn = false
    }

    private fun shouldSeeBiometricOptIn() =
        credChecker.biometricStatus() == SUCCESS && bioPrefHandler.getBioPref() == null
}
