package uk.gov.onelogin.login.ui.welcome

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.LoginSessionConfiguration
import uk.gov.android.authentication.TokenResponse
import uk.gov.android.features.FeatureFlags
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.onelogin.R
import uk.gov.onelogin.credentialchecker.BiometricStatus.SUCCESS
import uk.gov.onelogin.credentialchecker.CredentialChecker
import uk.gov.onelogin.features.StsFeatureFlag
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.login.usecase.VerifyIdToken
import uk.gov.onelogin.mainnav.nav.MainNavRoutes
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore
import uk.gov.onelogin.tokens.usecases.GetPersistentId
import uk.gov.onelogin.ui.LocaleUtils

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
    val onlineChecker: OnlineChecker
) : ViewModel() {
    private val tag = this::class.java.simpleName

    private val _next = MutableLiveData<String>()
    val next: LiveData<String> = _next

    fun onPrimary(
        launcher: ActivityResultLauncher<Intent>
    ) {
        val authorizeEndpoint = Uri.parse(
            context.resources.getString(
                if (featureFlags[StsFeatureFlag.STS_ENDPOINT]) {
                    R.string.stsUrl
                } else {
                    R.string.openIdConnectBaseUrl
                },
                context.resources.getString(R.string.openIdConnectAuthorizeEndpoint)
            )
        )
        val tokenEndpoint = Uri.parse(
            context.resources.getString(
                if (featureFlags[StsFeatureFlag.STS_ENDPOINT]) {
                    R.string.stsUrl
                } else {
                    R.string.apiBaseUrl
                },
                context.resources.getString(R.string.tokenExchangeEndpoint)
            )
        )
        val redirectUri = Uri.parse(
            context.resources.getString(
                R.string.webBaseUrl,
                context.resources.getString(R.string.webRedirectEndpoint)
            )
        )
        val clientId = if (featureFlags[StsFeatureFlag.STS_ENDPOINT]) {
            context.resources.getString(R.string.stsClientId)
        } else {
            context.resources.getString(R.string.openIdConnectClientId)
        }

        val scopes = listOf(LoginSessionConfiguration.Scope.OPENID)

        val locale = LocaleUtils.getLocaleAsSessionConfig(context)

        viewModelScope.launch {
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
                        persistentSessionId = getPersistentId()
                    )
                )
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun handleActivityResult(intent: Intent) {
        if (intent.data == null) return

        try {
            loginSession.finalise(intent = intent) { tokens ->
                handleTokens(tokens)
            }
        } catch (e: Throwable) { // handle both Error and Exception types.
            // Includes AuthenticationError
            Log.e(tag, e.message, e)
            _next.value = LoginRoutes.SIGN_IN_ERROR
        }
    }

    private fun handleTokens(tokens: TokenResponse) {
        val jwksUrl = context.getString(
            R.string.stsUrl,
            context.getString(R.string.jwksEndpoint)
        )

        viewModelScope.launch {
            tokens.idToken?.let { idToken ->
                if (!verifyIdToken(idToken, jwksUrl)) {
                    _next.value = LoginRoutes.SIGN_IN_ERROR
                } else {
                    checkLocalAuthRoute(tokens)
                }
            } ?: checkLocalAuthRoute(tokens)
        }
    }

    private fun checkLocalAuthRoute(tokens: TokenResponse) {
        tokenRepository.setTokenResponse(tokens)

        if (!credChecker.isDeviceSecure()) {
            bioPrefHandler.setBioPref(BiometricPreference.NONE)
            _next.value = LoginRoutes.PASSCODE_INFO
        } else if (shouldSeeBiometricOptIn()) {
            _next.value = LoginRoutes.BIO_OPT_IN
        } else {
            bioPrefHandler.setBioPref(BiometricPreference.PASSCODE)
            autoInitialiseSecureStore()
            _next.value = MainNavRoutes.START
        }
    }

    private fun shouldSeeBiometricOptIn() =
        credChecker.biometricStatus() == SUCCESS && bioPrefHandler.getBioPref() == null
}
