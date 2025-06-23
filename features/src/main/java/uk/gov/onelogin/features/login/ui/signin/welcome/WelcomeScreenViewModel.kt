package uk.gov.onelogin.features.login.ui.signin.welcome

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.android.authentication.login.AuthenticationError
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.LocalAuthManagerCallbackHandler
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.data.MainNavRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken
import uk.gov.onelogin.core.tokens.domain.save.SavePersistentId
import uk.gov.onelogin.core.tokens.domain.save.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.login.domain.signin.loginredirect.HandleLoginRedirect
import uk.gov.onelogin.features.login.domain.signin.remotelogin.HandleRemoteLogin
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@HiltViewModel
@Suppress("LongParameterList")
class WelcomeScreenViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val localAuthManager: LocalAuthManager,
    private val tokenRepository: TokenRepository,
    private val autoInitialiseSecureStore: AutoInitialiseSecureStore,
    private val verifyIdToken: VerifyIdToken,
    private val navigator: Navigator,
    private val saveTokens: SaveTokens,
    private val savePersistentId: SavePersistentId,
    private val saveTokenExpiry: SaveTokenExpiry,
    private val handleRemoteLogin: HandleRemoteLogin,
    private val handleLoginRedirect: HandleLoginRedirect,
    private val signOutUseCase: SignOutUseCase,
    private val logger: Logger,
    private val featureFlags: FeatureFlags,
    val onlineChecker: OnlineChecker
) : ViewModel() {
    private val tag = this::class.java.simpleName
    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun onPrimary(launcher: ActivityResultLauncher<Intent>) =
        viewModelScope.launch {
            _loading.emit(true)
            handleRemoteLogin.login(
                launcher
            ) {
                navigator.navigate(LoginRoutes.SignInError)
            }
        }

    fun handleActivityResult(
        intent: Intent,
        isReAuth: Boolean = false,
        activity: FragmentActivity
    ) {
        if (intent.data == null) return

        viewModelScope.launch {
            _loading.emit(true)
            handleLoginRedirect.handle(
                intent,
                onSuccess = {
                    viewModelScope.launch {
                        handleTokens(it, isReAuth, activity)
                    }
                },
                onFailure = {
                    it?.let {
                        logger.error(tag, it.message.toString(), it)
                    }
                    handleLoginErrors(it)
                }
            )
        }
    }

    fun abortLogin(launcher: ActivityResultLauncher<Intent>) {
        _loading.value = false
        onPrimary(launcher).cancel()
    }

    private fun CoroutineScope.handleLoginErrors(it: Throwable?) {
        this.launch {
            when (it) {
                is AuthenticationError -> {
                    when (it.type) {
                        AuthenticationError.ErrorType.ACCESS_DENIED -> {
                            signOutUseCase.invoke()
                            navigator.navigate(SignOutRoutes.ReAuthError)
                        }
                        else -> navigator.navigate(LoginRoutes.SignInError, true)
                    }
                }
                else -> navigator.navigate(LoginRoutes.SignInError, true)
            }
        }
    }

    fun navigateToDevPanel() {
        navigator.openDeveloperPanel()
    }

    fun navigateToOfflineError() {
        navigator.navigate(ErrorRoutes.Offline)
    }

    fun stopLoading() {
        _loading.value = false
    }

    private suspend fun handleTokens(
        tokens: TokenResponse,
        isReAuth: Boolean,
        activity: FragmentActivity
    ) {
        val jwksUrl = context.getString(
            R.string.stsUrl,
            context.getString(R.string.jwksEndpoint)
        )

        if (!verifyIdToken(tokens.idToken, jwksUrl)) {
            navigator.navigate(LoginRoutes.SignInError, true)
        } else {
            checkLocalAuthRoute(tokens, isReAuth, activity)
        }
    }

    private suspend fun checkLocalAuthRoute(
        tokens: TokenResponse,
        isReAuth: Boolean,
        activity: FragmentActivity
    ) {
        tokenRepository.setTokenResponse(tokens)
        saveTokenExpiry(tokens.accessTokenExpirationTime)
        savePersistentId()

        localAuthManager.enforceAndSet(
            featureFlags[WalletFeatureFlag.ENABLED],
            false,
            activity = activity,
            callbackHandler = object : LocalAuthManagerCallbackHandler {
                override fun onSuccess(backButtonPressed: Boolean) {
                    val pref = localAuthManager.localAuthPreference
                    when {
                        isReAuth -> {
                            if (pref is LocalAuthPreference.Enabled) {
                                viewModelScope.launch {
                                    saveTokens()
                                    navigator.goBack()
                                }
                            } else {
                                navigator.goBack()
                            }
                        }

                        else -> {
                            viewModelScope.launch {
                                autoInitialiseSecureStore.initialise()
                                navigator.navigate(MainNavRoutes.Start, true)
                            }
                        }
                    }
                }

                override fun onFailure(backButtonPressed: Boolean) {
                    navigator.navigate(MainNavRoutes.Start, true)
                }
            }
        )
    }
}
