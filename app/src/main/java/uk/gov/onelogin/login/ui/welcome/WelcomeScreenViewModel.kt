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
import kotlin.coroutines.resume
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
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

    @Suppress("TooGenericExceptionCaught", "ReturnCount")
    suspend fun handleActivityResult(intent: Intent): String? {
        if (intent.data == null) return null

        try {
            return suspendCancellableCoroutine<String> { continuation ->
                loginSession.finalise(intent = intent) { tokens ->
                    viewModelScope.launch {
                        continuation.resume(handleTokens(tokens))
                    }
                }
            }
        } catch (e: Throwable) { // handle both Error and Exception types.
            // Includes AuthenticationError
            Log.e(tag, e.message, e)
            return LoginRoutes.SIGN_IN_ERROR
        }
    }

    private suspend fun handleTokens(tokens: TokenResponse): String {
        val jwksUrl = context.getString(
            R.string.stsUrl,
            context.getString(R.string.jwksEndpoint)
        )

        return tokens.idToken?.let { idToken ->
            if (!verifyIdToken(idToken, jwksUrl)) {
                LoginRoutes.SIGN_IN_ERROR
            } else {
                checkLocalAuthRoute(tokens)
            }
        } ?: checkLocalAuthRoute(tokens)
    }

    @Suppress("ReturnCount")
    private fun checkLocalAuthRoute(tokens: TokenResponse): String {
        tokenRepository.setTokenResponse(tokens)

        if (!credChecker.isDeviceSecure()) {
            bioPrefHandler.setBioPref(BiometricPreference.NONE)
            return LoginRoutes.PASSCODE_INFO
        } else if (shouldSeeBiometricOptIn()) {
            return LoginRoutes.BIO_OPT_IN
        } else {
            bioPrefHandler.setBioPref(BiometricPreference.PASSCODE)
            autoInitialiseSecureStore()
            return MainNavRoutes.START
        }
    }

    private fun shouldSeeBiometricOptIn() =
        credChecker.biometricStatus() == SUCCESS && bioPrefHandler.getBioPref() == null
}
