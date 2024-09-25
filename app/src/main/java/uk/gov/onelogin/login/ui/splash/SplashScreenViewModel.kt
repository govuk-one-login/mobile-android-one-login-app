package uk.gov.onelogin.login.ui.splash

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.state.LocalAuthStatus
import uk.gov.onelogin.login.usecase.HandleLogin
import uk.gov.onelogin.mainnav.MainNavRoutes
import uk.gov.onelogin.navigation.NavRoute
import uk.gov.onelogin.navigation.Navigator

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val navigator: Navigator,
    private val handleLogin: HandleLogin
) : ViewModel(), DefaultLifecycleObserver {

    private val timesResumed: AtomicInteger = AtomicInteger(0)
    private val _showUnlock = mutableStateOf(false)
    val showUnlock: State<Boolean> = _showUnlock

    fun login(fragmentActivity: FragmentActivity, fromLockScreen: Boolean = false) {
        if (fromLockScreen && timesResumed.get() == 1) return
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

    private fun nextScreen(route: NavRoute) {
        val comingFromLockScreen = navigator.hasBackStack()
        val authSuccessful = route == MainNavRoutes.Start
        if (comingFromLockScreen && authSuccessful) {
            navigator.goBack()
        } else {
            navigator.navigate(route, true)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        timesResumed.addAndGet(1)
        super.onResume(owner)
    }
}
