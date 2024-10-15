package uk.gov.onelogin.login.ui.splash

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.onelogin.appinfo.service.domain.AppInfoService
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState
import uk.gov.onelogin.features.domain.SetFeatureFlags
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.state.LocalAuthStatus
import uk.gov.onelogin.login.usecase.HandleLogin
import uk.gov.onelogin.mainnav.MainNavRoutes
import uk.gov.onelogin.navigation.NavRoute
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.signOut.SignOutRoutes
import uk.gov.onelogin.ui.error.ErrorRoutes

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val navigator: Navigator,
    private val handleLogin: HandleLogin,
    private val appInfoService: AppInfoService,
    private val setFeatureFlags: SetFeatureFlags
) : ViewModel(), DefaultLifecycleObserver {

    private val _showUnlock = mutableStateOf(false)
    val showUnlock: State<Boolean> = _showUnlock

    fun login(fragmentActivity: FragmentActivity) {
        viewModelScope.launch {
            handleLogin(
                fragmentActivity,
                callback = {
                    when (it) {
                        LocalAuthStatus.SecureStoreError ->
                            nextScreen(LoginRoutes.SignInError)

                        LocalAuthStatus.ManualSignIn ->
                            nextScreen(LoginRoutes.Welcome)

                        is LocalAuthStatus.Success ->
                            nextScreen(MainNavRoutes.Start)

                        LocalAuthStatus.UserCancelled ->
                            _showUnlock.value = true

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

    fun retrieveAppInfo() {
        viewModelScope.launch {
            when (appInfoService.get()) {
                AppInfoServiceState.Offline -> navigateToOfflineError()
                AppInfoServiceState.Unavailable -> navigateToGenericError()
                else ->
                    setFeatureFlags.fromAppInfo()
            }
        }
    }

    private fun navigateToGenericError() {
        navigator.navigate(ErrorRoutes.Generic)
    }

    private fun navigateToOfflineError() {
        navigator.navigate(ErrorRoutes.Offline)
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
