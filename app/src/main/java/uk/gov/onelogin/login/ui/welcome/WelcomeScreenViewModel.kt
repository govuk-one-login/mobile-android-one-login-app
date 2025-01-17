package uk.gov.onelogin.login.ui.welcome

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.onelogin.R
import uk.gov.onelogin.credentialchecker.BiometricStatus.SUCCESS
import uk.gov.onelogin.credentialchecker.CredentialChecker
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.login.usecase.HandleLoginRedirect
import uk.gov.onelogin.login.usecase.HandleRemoteLogin
import uk.gov.onelogin.login.usecase.SaveTokens
import uk.gov.onelogin.login.usecase.VerifyIdToken
import uk.gov.onelogin.mainnav.MainNavRoutes
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiry
import uk.gov.onelogin.ui.error.ErrorRoutes

@HiltViewModel
@Suppress("LongParameterList")
class WelcomeScreenViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val credChecker: CredentialChecker,
    private val bioPrefHandler: BiometricPreferenceHandler,
    private val tokenRepository: TokenRepository,
    private val autoInitialiseSecureStore: AutoInitialiseSecureStore,
    private val verifyIdToken: VerifyIdToken,
    private val navigator: Navigator,
    private val saveTokens: SaveTokens,
    private val saveTokenExpiry: SaveTokenExpiry,
    private val handleRemoteLogin: HandleRemoteLogin,
    private val handleLoginRedirect: HandleLoginRedirect,
    val onlineChecker: OnlineChecker
) : ViewModel() {
    private val tag = this::class.java.simpleName
    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun onPrimary(
        launcher: ActivityResultLauncher<Intent>
    ) = viewModelScope.launch {
        _loading.emit(true)
        handleRemoteLogin.login(
            launcher
        ) {
            navigator.navigate(LoginRoutes.SignInError)
        }
        _loading.emit(false)
    }

    fun handleActivityResult(intent: Intent, isReAuth: Boolean = false) {
        if (intent.data == null) return

        viewModelScope.launch {
            _loading.emit(true)
            handleLoginRedirect.handle(
                intent,
                onSuccess = {
                    viewModelScope.launch {
                        handleTokens(it, isReAuth)
                    }
                },
                onFailure = {
                    Log.e(tag, it?.message, it)
                    navigator.navigate(LoginRoutes.SignInError, true)
                }
            )
        }
    }

    fun navigateToDevPanel() {
        navigator.openDeveloperPanel()
    }

    fun navigateToOfflineError() {
        navigator.navigate(ErrorRoutes.Offline)
    }

    private suspend fun handleTokens(tokens: TokenResponse, isReAuth: Boolean) {
        val jwksUrl = context.getString(
            R.string.stsUrl,
            context.getString(R.string.jwksEndpoint)
        )

        if (!verifyIdToken(tokens.idToken, jwksUrl)) {
            navigator.navigate(LoginRoutes.SignInError, true)
        } else {
            checkLocalAuthRoute(tokens, isReAuth)
        }
    }

    private suspend fun checkLocalAuthRoute(tokens: TokenResponse, isReAuth: Boolean) {
        tokenRepository.setTokenResponse(tokens)
        saveTokenExpiry(tokens.accessTokenExpirationTime)

        when {
            isReAuth -> {
                if (credChecker.isDeviceSecure()) {
                    saveTokens()
                }
                navigator.goBack()
            }

            !credChecker.isDeviceSecure() -> {
                bioPrefHandler.setBioPref(BiometricPreference.NONE)
                navigator.navigate(LoginRoutes.PasscodeInfo, true)
            }

            shouldSeeBiometricOptIn() ->
                navigator.navigate(LoginRoutes.BioOptIn, true)

            else -> {
                if (bioPrefHandler.getBioPref() != BiometricPreference.BIOMETRICS) {
                    bioPrefHandler.setBioPref(BiometricPreference.PASSCODE)
                }
                viewModelScope.launch {
                    autoInitialiseSecureStore.initialise()
                }
                navigator.navigate(MainNavRoutes.Start, true)
            }
        }
    }

    private fun shouldSeeBiometricOptIn() =
        credChecker.biometricStatus() == SUCCESS &&
            (
                bioPrefHandler.getBioPref() == null ||
                    bioPrefHandler.getBioPref() == BiometricPreference.NONE
                )
}
