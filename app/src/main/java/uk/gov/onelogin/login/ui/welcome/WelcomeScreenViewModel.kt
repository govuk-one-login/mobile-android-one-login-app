package uk.gov.onelogin.login.ui.welcome

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import uk.gov.android.authentication.login.LoginSession
import uk.gov.android.authentication.login.LoginSessionConfiguration
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.features.FeatureFlags
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.onelogin.R
import uk.gov.onelogin.appcheck.AppIntegrity
import uk.gov.onelogin.appcheck.AppIntegrityResult
import uk.gov.onelogin.credentialchecker.BiometricStatus.SUCCESS
import uk.gov.onelogin.credentialchecker.CredentialChecker
import uk.gov.onelogin.features.StsFeatureFlag
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.login.usecase.SaveTokens
import uk.gov.onelogin.login.usecase.VerifyIdToken
import uk.gov.onelogin.mainnav.MainNavRoutes
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore
import uk.gov.onelogin.tokens.usecases.GetPersistentId
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiry
import uk.gov.onelogin.ui.LocaleUtils
import uk.gov.onelogin.ui.error.ErrorRoutes

@HiltViewModel
@Suppress("LongParameterList")
class WelcomeScreenViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val loginSession: LoginSession,
    private val credChecker: CredentialChecker,
    private val bioPrefHandler: BiometricPreferenceHandler,
    private val tokenRepository: TokenRepository,
    private val autoInitialiseSecureStore: AutoInitialiseSecureStore,
    private val verifyIdToken: VerifyIdToken,
    private val featureFlags: FeatureFlags,
    private val getPersistentId: GetPersistentId,
    private val navigator: Navigator,
    private val localeUtils: LocaleUtils,
    private val saveTokens: SaveTokens,
    private val saveTokenExpiry: SaveTokenExpiry,
    val onlineChecker: OnlineChecker,
    private val appIntegrity: AppIntegrity
) : ViewModel() {
    private val tag = this::class.java.simpleName
    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun onPrimary(
        launcher: ActivityResultLauncher<Intent>
    ) {
        val authorizeEndpoint = Uri.parse(
            context.getString(
                if (featureFlags[StsFeatureFlag.STS_ENDPOINT]) {
                    R.string.stsUrl
                } else {
                    R.string.openIdConnectBaseUrl
                },
                context.getString(R.string.openIdConnectAuthorizeEndpoint)
            )
        )
        val tokenEndpoint = Uri.parse(
            context.getString(
                if (featureFlags[StsFeatureFlag.STS_ENDPOINT]) {
                    R.string.stsUrl
                } else {
                    R.string.apiBaseUrl
                },
                context.getString(R.string.tokenExchangeEndpoint)
            )
        )
        val redirectUri = Uri.parse(
            context.getString(
                R.string.webBaseUrl,
                context.getString(R.string.webRedirectEndpoint)
            )
        )
        val clientId = if (featureFlags[StsFeatureFlag.STS_ENDPOINT]) {
            context.getString(R.string.stsClientId)
        } else {
            context.getString(R.string.openIdConnectClientId)
        }
        val scopes = listOf(LoginSessionConfiguration.Scope.OPENID)
        val locale = localeUtils.getLocaleAsSessionConfig()
        viewModelScope.launch {
            val persistentId = getPersistentId()?.takeIf { it.isNotEmpty() }
            _loading.emit(true)
            when (appIntegrity.startCheck()) {
                is AppIntegrityResult.Failure -> handleAppIntegrityFailure()
                else -> {
                    _loading.emit(false)
                    loginSession
                        .present(
                            launcher,
                            configuration = LoginSessionConfiguration(
                                authorizeEndpoint = authorizeEndpoint,
                                clientId = clientId,
                                locale = locale,
                                redirectUri = redirectUri,
                                scopes = scopes,
                                tokenEndpoint = tokenEndpoint,
                                persistentSessionId = persistentId
                            )
                        )
                }
            }
        }
    }

    private suspend fun handleAppIntegrityFailure() {
        _loading.emit(false)
        navigator.navigate(LoginRoutes.SignInError)
    }

    @Suppress("TooGenericExceptionCaught")
    fun handleActivityResult(intent: Intent, isReAuth: Boolean = false) {
        if (intent.data == null) return

        viewModelScope.launch {
            try {
                loginSession.finalise(intent = intent) { tokens ->
                    viewModelScope.launch {
                        handleTokens(tokens, isReAuth)
                    }
                }
            } catch (e: Throwable) { // handle both Error and Exception types.
                // Includes AuthenticationError
                Log.e(tag, e.message, e)
                navigator.navigate(LoginRoutes.SignInError, true)
            }
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

        return tokens.idToken?.let { idToken ->
            if (!verifyIdToken(idToken, jwksUrl)) {
                navigator.navigate(LoginRoutes.SignInError, true)
            } else {
                checkLocalAuthRoute(tokens, isReAuth)
            }
        } ?: checkLocalAuthRoute(tokens, isReAuth)
    }

    @Suppress("ReturnCount")
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
                autoInitialiseSecureStore()
                saveTokens()
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
