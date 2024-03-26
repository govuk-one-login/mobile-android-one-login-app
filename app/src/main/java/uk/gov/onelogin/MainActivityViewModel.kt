package uk.gov.onelogin

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.android.authentication.LoginSession
import uk.gov.onelogin.credentialchecker.BiometricStatus
import uk.gov.onelogin.credentialchecker.CredentialChecker
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.ui.error.ErrorRoutes
import uk.gov.onelogin.ui.home.HomeRoutes

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val appRoutes: IAppRoutes,
    private val loginSession: LoginSession,
    private val credChecker: CredentialChecker,
    private val bioPrefHandler: BiometricPreferenceHandler,
    private val tokenRepository: TokenRepository
) : ViewModel() {
    private val tag = this::class.java.simpleName

    private val _next = MutableLiveData<String>()
    val next: LiveData<String> = _next

    fun handleIntent(
        intent: Intent?
    ) {
        val data = intent?.data
        when {
            data != null -> handleActivityResult(intent)
            tokensAvailable() -> _next.value = HomeRoutes.START
            else -> _next.value = LoginRoutes.START
        }
    }

    private fun tokensAvailable(): Boolean {
        return tokenRepository.getTokenResponse() != null
    }

    @Suppress("TooGenericExceptionCaught")
    private fun handleActivityResult(intent: Intent) {
        try {
            loginSession.finalise(intent = intent) { tokens ->
                tokenRepository.setTokenResponse(tokens)

                if (credChecker.isDeviceSecure()) {
                    when (credChecker.biometricStatus()) {
                        BiometricStatus.SUCCESS -> _next.value = LoginRoutes.BIO_OPT_IN
                        else -> _next.value = HomeRoutes.START
                    }
                } else {
                    bioPrefHandler.setBioPref(BiometricPreference.NONE)
                    _next.value = LoginRoutes.PASSCODE_INFO
                }
            }
        } catch (e: Throwable) { // handle both Error and Exception types
            Log.e(tag, e.message, e)
            _next.value = ErrorRoutes.ROOT
        }
    }
}
