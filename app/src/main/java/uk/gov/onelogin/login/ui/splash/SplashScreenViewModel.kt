package uk.gov.onelogin.login.ui.splash

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.gov.onelogin.appinfo.service.domain.AppInfoService
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.state.LocalAuthStatus
import uk.gov.onelogin.login.usecase.HandleLocalLogin
import uk.gov.onelogin.mainnav.MainNavRoutes
import uk.gov.onelogin.navigation.NavRoute
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.signOut.SignOutRoutes
import uk.gov.onelogin.ui.error.ErrorRoutes

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val navigator: Navigator,
    private val handleLocalLogin: HandleLocalLogin,
    private val appInfoService: AppInfoService
) : ViewModel(), DefaultLifecycleObserver {

    private val _showUnlock = mutableStateOf(false)
    val showUnlock: State<Boolean> = _showUnlock

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun login(fragmentActivity: FragmentActivity) {
        viewModelScope.launch {
            handleLocalLogin(
                fragmentActivity,
                callback = {
                    when (it) {
                        LocalAuthStatus.SecureStoreError ->
                            nextScreen(LoginRoutes.SignInError)

                        LocalAuthStatus.ManualSignIn ->
                            nextScreen(LoginRoutes.Welcome)

                        is LocalAuthStatus.Success ->
                            nextScreen(MainNavRoutes.Start)

                        LocalAuthStatus.UserCancelled -> {
                            _loading.value = false
                            _showUnlock.value = true
                        }

                        LocalAuthStatus.BioCheckFailed -> {
                            // Allow user to make multiple fails... do nothing for now
                        }

                        LocalAuthStatus.ReAuthSignIn ->
                            nextScreen(SignOutRoutes.Info)
                    }
                }
            )
        }
    }

    fun navigateToDevPanel() {
        navigator.openDeveloperPanel()
    }

    fun navigateToAnalyticsOptIn() {
        navigator.navigate(LoginRoutes.AnalyticsOptIn)
    }

    fun retrieveAppInfo(onSuccess: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            _loading.emit(true)
            when (appInfoService.get()) {
                AppInfoServiceState.Offline -> navigateToOfflineError()
                AppInfoServiceState.Unavailable -> navigateToAppUnavailableError()
                AppInfoServiceState.UpdateRequired -> navigateToUpdateRequiredError()
                // WHEN successful AppInfo response/ status
                else -> onSuccess()
            }
        }
    }

    private fun navigateToAppUnavailableError() {
        navigator.navigate(ErrorRoutes.Unavailable)
    }

    private fun navigateToOfflineError() {
        navigator.navigate(ErrorRoutes.Offline)
    }

    private fun navigateToUpdateRequiredError() {
        navigator.navigate(ErrorRoutes.UpdateRequired)
    }

    private fun nextScreen(route: NavRoute) {
        val comingFromLockScreen = navigator.hasBackStack()
        val authSuccessful = route == MainNavRoutes.Start
        if (comingFromLockScreen && authSuccessful) {
            navigator.goBack()
        } else {
            navigator.goBack()
            navigator.navigate(route)
        }
    }
}
