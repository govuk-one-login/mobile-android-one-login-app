package uk.gov.onelogin.features.login.domain.signin.remotelogin

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.gov.android.authentication.login.AuthenticationError
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.LocalAuthManagerCallbackHandler
import uk.gov.android.localauth.preference.LocalAuthPreference
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
import uk.gov.onelogin.core.tokens.domain.remove.RemoveRefreshTokenAndExpiry
import uk.gov.onelogin.core.tokens.domain.save.SavePersistentId
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.ExpiryInfo
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.utils.convertToLoginTokens
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityException
import uk.gov.onelogin.features.login.domain.signin.remotelogin.finalise.FinaliseRemoteLogin
import uk.gov.onelogin.features.login.domain.signin.remotelogin.start.StartRemoteLogin
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@Suppress("LongParameterList")
class RemoteLoginImpl
    @Inject
    constructor(
        @param:ApplicationContext
        private val context: Context,
        private val finaliseRemoteLogin: FinaliseRemoteLogin,
        private val startRemoteLogin: StartRemoteLogin,
        private val localAuthManager: LocalAuthManager,
        private val tokenRepository: TokenRepository,
        private val verifyIdToken: VerifyIdToken,
        private val autoInitialiseSecureStore: AutoInitialiseSecureStore,
        private val savePersistentId: SavePersistentId,
        private val saveTokenExpiry: SaveTokenExpiry,
        private val signOutUseCase: SignOutUseCase,
        private val removeRefreshTokenAndExpiry: RemoveRefreshTokenAndExpiry,
        private val errorCounter: Counter,
        private val logger: Logger,
        private val navigator: Navigator
    ) : RemoteLogin {
        override suspend fun start(launcher: ActivityResultLauncher<Intent>) {
            startRemoteLogin.login(
                launcher,
            ) { throwable ->
                when (throwable) {
                    is AppIntegrityException.ClientAttestationException,
                    is AppIntegrityException.Other,
                    is AppIntegrityException.FirebaseException -> {
                        navigator.navigate(ErrorRoutes.AppIntegrity)
                    }

                    else -> navigator.navigate(LoginRoutes.SignInRecoverableError)
                }
            }
        }

        override suspend fun finalise(
            intent: Intent,
            isReAuth: Boolean,
            activity: FragmentActivity,
        ) {
            // Enables running all functions in respective order as this cannot be done async
            // and allows to use teh same Coroutine scope avoiding multithreading
            val deferred = CompletableDeferred<Result<TokenResponse>>()
            finaliseRemoteLogin.handle(
                intent,
                onSuccess = {
                    deferred.complete(Result.success(it))
                },
                onFailure = {
                    deferred
                        .complete(
                            Result.failure(
                                it
                                    ?: Exception("Login finalise failed")
                            )
                        )
                }
            )
            coroutineScope {
                deferred.await().fold(
                    onSuccess = { handleTokens(it, isReAuth, activity) },
                    onFailure = {
                        val loginException = LoginException(it)
                        logger.error(
                            loginException.javaClass.simpleName,
                            it.message.toString(),
                            loginException,
                        )
                        handleLoginErrors(it)
                    }
                )
            }
        }

        private fun CoroutineScope.handleTokens(
            tokens: TokenResponse,
            isReAuth: Boolean,
            activity: FragmentActivity,
        ) {
            errorCounter.reset()
            val jwksUrl =
                context.getString(
                    R.string.stsUrl,
                    context.getString(R.string.jwksEndpoint),
                )
            this.launch {
                if (!verifyIdToken(tokens.idToken, jwksUrl)) {
                    navigator.navigate(LoginRoutes.SignInRecoverableError, true)
                } else {
                    withContext(Dispatchers.Main) {
                        checkLocalAuthRouteAndSaveTokensAndExpiry(tokens, isReAuth, activity)
                    }
                }
            }
        }

        private suspend fun checkLocalAuthRouteAndSaveTokensAndExpiry(
            tokens: TokenResponse,
            isReAuth: Boolean,
            activity: FragmentActivity,
        ) {
            saveAccessTokenExpiryToOpenStore(tokens)
            tokenRepository.setTokenResponse(tokens.convertToLoginTokens())
            savePersistentId()

            localAuthManager.enforceAndSet(
                false,
                activity = activity,
                callbackHandler =
                    object : LocalAuthManagerCallbackHandler {
                        override fun onSuccess(backButtonPressed: Boolean) {
                            // Required to enable running suspend functions form within the callback handler
                            // Requires Main thread as it displays UI that needs to be persisted until user allows for continuation of the flow to the other screens
                            CoroutineScope(Dispatchers.Main).launch {
                                if (isReAuth) {
                                    handleLocalAuthCallbackReAuth(localAuthManager.localAuthPreference, tokens)
                                } else {
                                    handleLocalAuthCallbackNoReAuth(localAuthManager.localAuthPreference, tokens)
                                }
                            }
                        }

                        override fun onFailure(backButtonPressed: Boolean) {
                            navigator.navigate(MainNavRoutes.Start, true)
                        }
                    },
            )
        }

        private suspend fun handleLocalAuthCallbackReAuth(
            pref: LocalAuthPreference?,
            tokens: TokenResponse
        ) {
            if (pref is LocalAuthPreference.Enabled) {
                // We always save the tokens and pass in the refresh one because if no refresh token was returned it will be null, but if populated we should save it
                autoInitialiseSecureStore.initialise(tokens.refreshToken)
                if (tokens.refreshToken != null) {
                    saveRefreshTokenExpiryToOpenStore(tokens)
                } else {
                    removeRefreshTokenAndExpiry.remove()
                }
                navigator.goBack()
            } else {
                navigator.goBack()
            }
        }

        private suspend fun handleLocalAuthCallbackNoReAuth(
            pref: LocalAuthPreference?,
            tokens: TokenResponse
        ) {
            // We always save the tokens and pass in the refresh one because if no refresh token was returned it will be null, but if populated we should save it
            autoInitialiseSecureStore.initialise(tokens.refreshToken)
            if (tokens.refreshToken != null) {
                if (pref is LocalAuthPreference.Enabled) {
                    saveRefreshTokenExpiryToOpenStore(tokens)
                }
            } else {
                removeRefreshTokenAndExpiry.remove()
            }
            navigator.navigate(MainNavRoutes.Start, true)
        }

        private suspend fun saveRefreshTokenExpiryToOpenStore(tokens: TokenResponse) {
            tokens.refreshToken?.let {
                val extractedExp = saveTokenExpiry.extractExpFromRefreshToken(it)
                saveTokenExpiry.saveExp(
                    ExpiryInfo(
                        key = REFRESH_TOKEN_EXPIRY_KEY,
                        value = extractedExp,
                    ),
                )
            }
        }

        private suspend fun saveAccessTokenExpiryToOpenStore(tokens: TokenResponse) {
            saveTokenExpiry.saveExp(
                ExpiryInfo(
                    key = ACCESS_TOKEN_EXPIRY_KEY,
                    value = tokens.accessTokenExpirationTime,
                ),
            )
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

                is AppIntegrityException.ClientAttestationException,
                is AppIntegrityException.Other,
                is AppIntegrityException.FirebaseException -> {
                    navigator.navigate(ErrorRoutes.AppIntegrity)
                }

                else -> navigator.navigate(LoginRoutes.SignInRecoverableError, true)
            }
        }
    }

private const val MAX_ATTEMPTS = 3
