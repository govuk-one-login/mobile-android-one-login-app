package uk.gov.onelogin.features.login

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.android.network.online.OnlineChecker
import uk.gov.logging.api.LogTagProvider
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.localauth.domain.LocalAuthPrefResetUseCase
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
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
    ) : ViewModel(), LogTagProvider {
        private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val loading = _loading.asStateFlow()

        private var startJob: Job? = null

        fun startLoginActivity(
            launcher: ActivityResultLauncher<Intent>,
            isReAuth: Boolean
        ) = viewModelScope.launch {
            _loading.emit(true)
            if (!onlineChecker.isOnline()) {
                navigator.navigate(ErrorRoutes.Offline)
                return@launch
            }

            // If persistent session ID is empty on re-auth, that suggests something went wrong and we delete all data
            if (isReAuth && getPersistentId().isNullOrEmpty()) {
                try {
                    signOutUseCase.invoke()
                    navigator.navigate(SignOutRoutes.ReAuthError, true)
                } catch (_: SignOutError) {
                    navigator.navigate(LoginRoutes.SignInUnrecoverableError, true)
                }
                return@launch
            }

            // This allows for the BiometricOptIn prompt to be displayed anytime a re-auth is done ONLY IF the
            // user eith has no preference (something went wrong) or opted out at an earlier time
            localAuthPrefResetUseCase.reset()
            remoteLogin.start(launcher)
        }.also { job ->
            startJob = job
        }

        fun handleLoginActivityResult(
            result: ActivityResult,
            isReAuth: Boolean = false,
            activity: FragmentActivity,
        ) {
            if (result.resultCode != Activity.RESULT_OK) {
                _loading.value = false
                return
            }

            val intent = result.data ?: run {
                logger.error(
                    NULL_INTENT_MSG,
                    Exception(NULL_INTENT_MSG),
                    componentKey(COMPONENT_LOGIN),
                    actionKey(ACTION_START_LOGIN_RESULT),
                )
                _loading.value = false
                return
            }

            viewModelScope.launch {
                _loading.emit(true)
                remoteLogin.finalise(
                    intent,
                    isReAuth,
                    activity
                )
            }
        }

        fun abortLogin() {
            _loading.value = false
            startJob?.cancel()
            startJob = null
        }

        fun stopLoading() {
            _loading.value = false
        }

        companion object {
            internal const val COMPONENT_LOGIN = "login"
            internal const val ACTION_START_LOGIN_RESULT = "Start Login Result"
            internal const val NULL_INTENT_MSG = "Intent data is null"
        }
    }
