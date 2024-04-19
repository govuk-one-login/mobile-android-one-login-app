package uk.gov.onelogin

import android.content.Intent
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.android.authentication.LoginSession
import uk.gov.onelogin.credentialchecker.BiometricStatus.SUCCESS
import uk.gov.onelogin.credentialchecker.CredentialChecker
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore
import uk.gov.onelogin.ui.error.ErrorRoutes
import uk.gov.onelogin.ui.home.HomeRoutes

@HiltViewModel
@Suppress("LongParameterList")
class MainActivityViewModel @Inject constructor(
    val appRoutes: IAppRoutes,
    private val loginSession: LoginSession,
    private val credChecker: CredentialChecker,
    private val bioPrefHandler: BiometricPreferenceHandler,
    private val tokenRepository: TokenRepository,
    autoInitialiseSecureStore: AutoInitialiseSecureStore
) : ViewModel(), DefaultLifecycleObserver {
    private val tag = this::class.java.simpleName

    private val _next = MutableLiveData<String>()
    val next: LiveData<String> = _next

    init {
        autoInitialiseSecureStore()
    }

    @Suppress("TooGenericExceptionCaught")
    fun handleActivityResult(intent: Intent) {
        if (intent.data == null) return
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
    }

    private fun shouldSeeBiometricOptIn() =
        credChecker.biometricStatus() == SUCCESS && bioPrefHandler.getBioPref() == null

    override fun onPause(owner: LifecycleOwner) {
        if (bioPrefHandler.getBioPref() != BiometricPreference.NONE &&
            tokenRepository.getTokenResponse() != null
        ) {
            tokenRepository.clearTokenResponse()
            _next.value = LoginRoutes.START
        }
    }
}
