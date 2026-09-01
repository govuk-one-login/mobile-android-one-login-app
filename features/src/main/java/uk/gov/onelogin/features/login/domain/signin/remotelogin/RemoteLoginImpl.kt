package uk.gov.onelogin.features.login.domain.signin.remotelogin

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import uk.gov.android.authentication.login.AuthenticationError
import uk.gov.android.authentication.login.TokenResponse
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.LocalAuthManagerCallbackHandler
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.v3.Logger
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
import javax.inject.Inject

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
        private val jwksUrl
            get() = context.getString(R.string.stsUrl, context.getString(R.string.jwksEndpoint))

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
            finaliseRemoteLogin.handleInternal(intent)
                .onSuccess {
                    errorCounter.reset()
                    handleTokens(it, isReAuth, activity)
                }
                .onFailure {
                    val loginException = LoginException(it)
                    logger.error(
                        loginException.javaClass.simpleName,
                        it.message.toString(),
                        loginException,
                    )
                    handleLoginErrors(it)
                }
        }

        private suspend fun handleTokens(
            tokens: TokenResponse,
            isReAuth: Boolean,
            activity: FragmentActivity,
        ) {
            if (!verifyIdToken(tokens.idToken, jwksUrl)) {
                navigator.navigate(LoginRoutes.SignInRecoverableError, true)
                return
            }

            // Saved all data that is not dependent on local auth being enabled (access token expiry, sets the memory/ singleton token response that gets reset when the app is closed
            // to the token response just received and saves the persistent session id)
            saveAccessTokenExpiryToOpenStore(tokens)
            tokenRepository.setTokenResponse(tokens.convertToLoginTokens())
            savePersistentId()

            localAuthManager.enforceAndSetInternal(activity)
                .onFailure {
                    navigator.navigate(MainNavRoutes.Start, true)
                }
                .onSuccess { localAuthPreference ->
                    val refreshToken = tokens.refreshToken
                    if (localAuthPreference is LocalAuthPreference.Enabled) {
                        // The refresh token may be null, but always initialise the secure store if possible
                        autoInitialiseSecureStore.initialise(refreshToken)

                        if (refreshToken != null) {
                            saveRefreshTokenExpiryToOpenStore(refreshToken)
                        }
                    }

                    if (refreshToken == null) {
                        removeRefreshTokenAndExpiry.remove()
                    }

                    if (isReAuth) {
                        navigator.goBack()
                    } else {
                        navigator.navigate(MainNavRoutes.Start, true)
                    }
                }
        }

        private suspend fun saveRefreshTokenExpiryToOpenStore(refreshToken: String) {
            val extractedExp = saveTokenExpiry.extractExpFromRefreshToken(refreshToken)
            saveTokenExpiry.saveExp(
                ExpiryInfo(
                    key = REFRESH_TOKEN_EXPIRY_KEY,
                    value = extractedExp,
                ),
            )
        }

        private suspend fun saveAccessTokenExpiryToOpenStore(tokens: TokenResponse) {
            saveTokenExpiry.saveExp(
                ExpiryInfo(
                    key = ACCESS_TOKEN_EXPIRY_KEY,
                    value = tokens.accessTokenExpirationTime,
                ),
            )
        }

        private suspend fun handleLoginErrors(it: Throwable?) {
            when (it) {
                is AuthenticationError -> {
                    when (it.type) {
                        AuthenticationError.ErrorType.ACCESS_DENIED -> {
                            signOutUseCase.invoke()
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

        private suspend fun FinaliseRemoteLogin.handleInternal(intent: Intent): Result<TokenResponse> =
            // Adapts the callback based API to a result
            CompletableDeferred<Result<TokenResponse>>()
                .also { deferred ->
                    handle(
                        intent,
                        onSuccess = {
                            deferred.complete(Result.success(it))
                        },
                        onFailure = {
                            val exception = it ?: IllegalStateException("Login finalise failed")
                            deferred.complete(Result.failure(exception))
                        }
                    )
                }
                .await()

        private suspend fun LocalAuthManager.enforceAndSetInternal(
            activity: FragmentActivity
        ) :Result<LocalAuthPreference?> =
            // Adapts the callback based API to a result
            CompletableDeferred<Result<LocalAuthPreference?>>()
                .also { deferred ->
                    enforceAndSet(
                        false,
                        activity = activity,
                        callbackHandler = object : LocalAuthManagerCallbackHandler {
                            override fun onSuccess(backButtonPressed: Boolean) {
                                deferred.complete(Result.success(localAuthPreference))
                            }

                            override fun onFailure(backButtonPressed: Boolean) {
                                deferred.complete(
                                    Result.failure(
                                    IllegalStateException("Failed to check local auth status")
                                    )
                                )
                            }
                        },
                    )
                }
                .await()
    }

private const val MAX_ATTEMPTS = 3
