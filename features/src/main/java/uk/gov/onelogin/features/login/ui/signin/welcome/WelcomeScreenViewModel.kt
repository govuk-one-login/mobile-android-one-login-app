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
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.LocalAuthManagerCallbackHandler
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.counter.Counter
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.data.MainNavRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.LoginException
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken
import uk.gov.onelogin.core.tokens.domain.save.SavePersistentId
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.ExpiryInfo
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.features.login.domain.signin.loginredirect.HandleLoginRedirect
import uk.gov.onelogin.features.login.domain.signin.remotelogin.HandleRemoteLogin
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@HiltViewModel
@Suppress("LongParameterList", "TooManyFunctions")
class WelcomeScreenViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val localAuthManager: LocalAuthManager,
    private val tokenRepository: TokenRepository,
    private val autoInitialiseSecureStore: AutoInitialiseSecureStore,
    private val verifyIdToken: VerifyIdToken,
    private val navigator: Navigator,
    private val savePersistentId: SavePersistentId,
    private val saveTokenExpiry: SaveTokenExpiry,
    private val handleRemoteLogin: HandleRemoteLogin,
    private val handleLoginRedirect: HandleLoginRedirect,
    private val signOutUseCase: SignOutUseCase,
    private val logger: Logger,
    val onlineChecker: OnlineChecker,
    private val errorCounter: Counter
) : ViewModel() {
    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun onPrimary(launcher: ActivityResultLauncher<Intent>) =
        viewModelScope.launch {
            _loading.emit(true)
            handleRemoteLogin.login(
                launcher
            ) {
                navigator.navigate(LoginRoutes.SignInRecoverableError)
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
                        val loginException = LoginException(it)
                        logger.error(
                            loginException.javaClass.simpleName,
                            it.message.toString(),
                            loginException
                        )
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
        when (it) {
            is AuthenticationError -> {
                when (it.type) {
                    AuthenticationError.ErrorType.ACCESS_DENIED -> {
                        this.launch {
                            signOutUseCase.invoke()
                        }
                        navigator.navigate(SignOutRoutes.ReAuthError)
                    }

                    AuthenticationError.ErrorType.SERVER_ERROR -> {
                        errorCounter.increment()
                        if (errorCounter.getValue() >= MAX_ATTEMPTS) {
                            navigator.navigate(LoginRoutes.SignInUnrecoverableError, true)
                        } else {
                            navigator.navigate(LoginRoutes.SignInRecoverableError, true)
                        }
                    }

                    AuthenticationError.ErrorType.TOKEN_ERROR ->
                        navigator.navigate(LoginRoutes.SignInUnrecoverableError, true)

                    else -> {
                        navigator.navigate(LoginRoutes.SignInUnrecoverableError, true)
                    }
                }
            }

            else -> navigator.navigate(LoginRoutes.SignInRecoverableError, true)
        }
    }

    fun navigateToDevPanel() {
        navigator.openDeveloperPanel()
    }

    fun navigateToOfflineError() {
        navigator.navigate(ErrorRoutes.Offline)
    }

    fun navigateToOptInAnalytics() {
        navigator.navigate(LoginRoutes.AnalyticsOptIn)
    }

    fun stopLoading() {
        _loading.value = false
    }

    private suspend fun handleTokens(
        tokens: TokenResponse,
        isReAuth: Boolean,
        activity: FragmentActivity
    ) {
        errorCounter.reset()
        val jwksUrl = context.getString(
            R.string.stsUrl,
            context.getString(R.string.jwksEndpoint)
        )
        if (!verifyIdToken(tokens.idToken, jwksUrl)) {
            navigator.navigate(LoginRoutes.SignInRecoverableError, true)
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
        saveAccessTokenExpiryToOpenStore(tokens)
        savePersistentId()

        localAuthManager.enforceAndSet(
            // Wallet is now permanently turned on - the work on LocalAuthManager to amend this will come at a later time
            true,
            false,
            activity = activity,
            callbackHandler = object : LocalAuthManagerCallbackHandler {
                override fun onSuccess(backButtonPressed: Boolean) {
                    val pref = localAuthManager.localAuthPreference
                    when {
                        isReAuth -> {
                            if (pref is LocalAuthPreference.Enabled) {
                                viewModelScope.launch {
                                    autoInitialiseSecureStore.initialise(tokens.refreshToken)
                                    saveRefreshTokenExpiryToOpenStore(tokens)
                                }
                            } else {
                                navigator.goBack()
                            }
                        }

                        else -> {
                            viewModelScope.launch {
                                if (pref is LocalAuthPreference.Enabled) {
                                    saveRefreshTokenExpiryToOpenStore(tokens)
                                }
                                autoInitialiseSecureStore.initialise(tokens.refreshToken)
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

    private fun saveRefreshTokenExpiryToOpenStore(tokens: TokenResponse) {
        tokens.refreshToken?.let {
            val extractedExp = saveTokenExpiry.extractExpFromRefreshToken(it)
            saveTokenExpiry.saveExp(
                ExpiryInfo(
                    key = REFRESH_TOKEN_EXPIRY_KEY,
                    value = extractedExp
                )
            )
        }
    }

    private fun saveAccessTokenExpiryToOpenStore(tokens: TokenResponse) {
        saveTokenExpiry.saveExp(
            ExpiryInfo(
                key = ACCESS_TOKEN_EXPIRY_KEY,
                value = tokens.accessTokenExpirationTime
            )
        )
    }
}

private const val MAX_ATTEMPTS = 3
