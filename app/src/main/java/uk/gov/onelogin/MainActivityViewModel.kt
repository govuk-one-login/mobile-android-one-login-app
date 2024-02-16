package uk.gov.onelogin

import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.credentialchecker.BiometricStatus
import uk.gov.onelogin.credentialchecker.CredentialChecker
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.ui.home.HomeRoutes
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val appRoutes: IAppRoutes,
    private val loginSession: LoginSession,
    private val credChecker: CredentialChecker
) : ViewModel() {
    private val tag = this::class.java.simpleName

    private val _next = MutableLiveData<String>()
    val next: LiveData<String> = _next

    fun handleIntent(
        intent: Intent?,
        sharedPrefs: SharedPreferences
    ) {
        val data = intent?.data
        when {
            data != null -> handleActivityResult(sharedPrefs, intent)
            tokensAvailable(sharedPrefs) -> _next.value = HomeRoutes.START
            else -> _next.value = LoginRoutes.START
        }
    }

    private fun storeTokens(
        sharedPrefs: SharedPreferences,
        tokens: TokenResponse
    ) {
        with(
            sharedPrefs.edit()
        ) {
            putString(TOKENS_PREFERENCES_KEY, tokens.jsonSerializeString())
            apply()
        }
    }

    private fun tokensAvailable(sharedPrefs: SharedPreferences): Boolean {
        return sharedPrefs.getString(TOKENS_PREFERENCES_KEY, null) != null
    }

    private fun handleActivityResult(sharedPrefs: SharedPreferences, intent: Intent) {
        try {
            loginSession.finalise(intent = intent) { tokens ->
                storeTokens(
                    sharedPrefs = sharedPrefs,
                    tokens = tokens
                )

                if (credChecker.isDeviceSecure()) {
                    when (credChecker.biometricStatus()) {
                        BiometricStatus.SUCCESS -> {
                            _next.value = LoginRoutes.BIO_OPT_IN
                        }

                        else -> {
                            _next.value = HomeRoutes.START
                        }
                    }
                } else {
                    _next.value = HomeRoutes.PASSCODE_ERROR
                }
            }
        } catch (e: Error) {
            _next.value = LoginRoutes.START
        }
    }

    companion object {
        const val TOKENS_PREFERENCES_FILE = "tokens"
        const val TOKENS_PREFERENCES_KEY = "authTokens"
    }
}
