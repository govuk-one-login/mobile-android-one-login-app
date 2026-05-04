package uk.gov.onelogin.features.login.ui.signin.welcome

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.android.network.online.OnlineChecker
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.login.domain.signin.remotelogin.RemoteLogin
import javax.inject.Inject

@HiltViewModel
@Suppress("LongParameterList", "TooManyFunctions")
class WelcomeScreenViewModel
    @Inject
    constructor(
        private val navigator: Navigator,
        val onlineChecker: OnlineChecker,
        private val remoteLogin: RemoteLogin,
    ) : ViewModel() {
        private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val loading = _loading.asStateFlow()

        fun onPrimary(launcher: ActivityResultLauncher<Intent>) =
            viewModelScope.launch {
                _loading.emit(true)
                remoteLogin.start(
                    launcher,
                )
            }

        fun handleActivityResult(
            intent: Intent,
            isReAuth: Boolean = false,
            activity: FragmentActivity,
        ) {
            if (intent.data == null) return

            viewModelScope.launch {
                _loading.emit(true)
                remoteLogin.finalise(
                    intent,
                    isReAuth,
                    activity
                )
            }
        }

        fun abortLogin(launcher: ActivityResultLauncher<Intent>) {
            _loading.value = false
            onPrimary(launcher).cancel()
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
    }
