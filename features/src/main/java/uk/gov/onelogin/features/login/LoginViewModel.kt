package uk.gov.onelogin.features.login

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.android.network.online.OnlineChecker
import uk.gov.logging.api.v2.Logger
import uk.gov.logging.api.v2.errorKeys.ErrorKeys
import uk.gov.onelogin.core.localauth.domain.LocalAuthPrefResetUseCase
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.features.login.domain.signin.remotelogin.RemoteLogin
import uk.gov.onelogin.features.signout.domain.SignOutError
import uk.gov.onelogin.features.signout.domain.SignOutUseCase
import javax.inject.Inject

@Suppress("LongParameterList")
@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val navigator: Navigator,
        private val onlineChecker: OnlineChecker,
        private val remoteLogin: RemoteLogin,
        private val getPersistentId: GetPersistentId,
        private val signOutUseCase: SignOutUseCase,
        private val localAuthPrefResetUseCase: LocalAuthPrefResetUseCase,
        private val logger: Logger
    ) : ViewModel() {
        private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val loading = _loading.asStateFlow()

        fun startLoginActivity(
            launcher: ActivityResultLauncher<Intent>,
            isReAuth: Boolean
        ) = viewModelScope.launch {
            _loading.emit(true)
            if (onlineChecker.isOnline()) {
                if (getPersistentId().isNullOrEmpty() && isReAuth) {
                    try {
                        signOutUseCase.invoke()
                        navigator.navigate(SignOutRoutes.ReAuthError, true)
                    } catch (_: SignOutError) {
                        navigator.navigate(LoginRoutes.SignInUnrecoverableError, true)
                    }
                } else {
                    localAuthPrefResetUseCase.reset()
                    remoteLogin.start(
                        launcher,
                    )
                }
            } else {
                navigator.navigate(ErrorRoutes.Offline)
            }
        }

        fun handleLoginActivityResult(
            result: ActivityResult,
            isReAuth: Boolean = false,
            activity: FragmentActivity,
        ) {
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    viewModelScope.launch {
                        _loading.emit(true)
                        remoteLogin.finalise(
                            intent,
                            isReAuth,
                            activity
                        )
                    }
                } ?: run {
                    logger.error(
                        LOGIN_START_RESULT_TAG,
                        NULL_INTENT_MSG,
                        Exception(NULL_INTENT_MSG),
                        ErrorKeys.StringKey(LOGIN_START_RESULT_TAG, NULL_INTENT_MSG)
                    )
                    return
                }
            }
        }

        fun abortLogin(
            launcher: ActivityResultLauncher<Intent>,
            isReAuth: Boolean
        ) {
            _loading.value = false
            startLoginActivity(launcher, isReAuth).cancel()
        }

        fun stopLoading() {
            _loading.value = false
        }

        companion object {
            internal const val LOGIN_START_RESULT_TAG = "Start Login Result"
            internal const val NULL_INTENT_MSG = "Intent data is null"
        }
    }
