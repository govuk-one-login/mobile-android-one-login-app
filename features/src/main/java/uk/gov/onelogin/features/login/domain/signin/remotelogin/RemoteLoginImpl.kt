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
        private val recoverableErrors: Counter,
        private val logger: Logger,
    ) : RemoteLogin {
        private val jwksUrl
            get() = context.getString(R.string.stsUrl, context.getString(R.string.jwksEndpoint))

        override suspend fun start(launcher: ActivityResultLauncher<Intent>): RemoteLogin.Result {
            val result = startRemoteLogin.login(launcher)

            if (result is StartRemoteLogin.Result.Failure) {
                return when (result.error) {
                    is AppIntegrityException.ClientAttestationException,
                    is AppIntegrityException.Other,
                    is AppIntegrityException.FirebaseException -> {
                        RemoteLogin.Result.Failure(RemoteLogin.FailureType.AppIntegrity)
                    }

                    else -> RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInRecoverable)
                }
            }

            return RemoteLogin.Result.Success
        }

        override suspend fun finalise(
            intent: Intent,
            activity: FragmentActivity,
        ): RemoteLogin.Result {
            val result = finaliseRemoteLogin.handle(intent)

            return when (result) {
                is FinaliseRemoteLogin.Result.Failure -> {
                    val loginException = LoginException(result.error)
                    logger.error(
                        loginException.javaClass.simpleName,
                        result.error.message.toString(),
                        loginException,
                    )

                    if (result.error is AuthenticationError &&
                        result.error.type ==
                        AuthenticationError.ErrorType.SERVER_ERROR
                    ) {
                        recoverableErrors.increment()
                    }

                    val failureResult = result.error.toLoginResult()

                    if (failureResult.type == RemoteLogin.FailureType.AccessDenied) {
                        signOutUseCase.invoke()
                    }

                    failureResult
                }

                is FinaliseRemoteLogin.Result.Success -> {
                    recoverableErrors.reset()
                    saveTokens(result.tokenResponse, activity)
                }
            }
        }

        private suspend fun saveTokens(
            tokens: TokenResponse,
            activity: FragmentActivity,
        ): RemoteLogin.Result {
            if (!verifyIdToken(tokens.idToken, jwksUrl)) {
                return RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInRecoverable)
            }

            // Saved all data that is not dependent on local auth being enabled (access token expiry, sets the memory/ singleton token response that gets reset when the app is closed
            // to the token response just received and saves the persistent session id)
            saveAccessTokenExpiryToOpenStore(tokens)
            tokenRepository.setTokenResponse(tokens.convertToLoginTokens())
            savePersistentId()

            val localAuthPreference = localAuthManager.enforceAndSetInternal(activity).getOrElse {
                RemoteLogin.Result.Success
            }

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

            return RemoteLogin.Result.Success
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

        private fun Throwable.toLoginResult(): RemoteLogin.Result.Failure =
            when (this) {
                is AuthenticationError -> {
                    when (this.type) {
                        AuthenticationError.ErrorType.ACCESS_DENIED -> {
                            RemoteLogin.Result.Failure(RemoteLogin.FailureType.AccessDenied)
                        }

                        AuthenticationError.ErrorType.SERVER_ERROR -> {
                            if (recoverableErrors.getValue() >= MAX_SERVER_ERRORS) {
                                RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInUnrecoverable)
                            } else {
                                RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInRecoverable)
                            }
                        }

                        AuthenticationError.ErrorType.TOKEN_ERROR ->
                            RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInUnrecoverable)

                        else -> {
                            RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInUnrecoverable)
                        }
                    }
                }

                is AppIntegrityException.ClientAttestationException,
                is AppIntegrityException.Other,
                is AppIntegrityException.FirebaseException -> {
                    RemoteLogin.Result.Failure(RemoteLogin.FailureType.AppIntegrity)
                }

                else ->
                    RemoteLogin.Result.Failure(RemoteLogin.FailureType.SignInRecoverable)
            }

        private suspend fun LocalAuthManager.enforceAndSetInternal(
            activity: FragmentActivity
        ): Result<LocalAuthPreference?> =
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

private const val MAX_SERVER_ERRORS = 3
