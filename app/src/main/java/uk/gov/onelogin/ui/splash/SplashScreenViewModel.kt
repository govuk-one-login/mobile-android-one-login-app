package uk.gov.onelogin.ui.splash

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.state.LocalAuthStatus
import uk.gov.onelogin.login.usecase.HandleLogin
import uk.gov.onelogin.ui.home.HomeRoutes

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val handleLogin: HandleLogin
) : ViewModel() {

    private val _showUnlock = mutableStateOf(false)
    val showUnlock: MutableState<Boolean> = _showUnlock

    private val _next = MutableLiveData<String>()
    val next: LiveData<String> = _next

    fun login(fragmentActivity: FragmentActivity) {
        viewModelScope.launch {
            handleLogin(
                fragmentActivity,
                callback = {
                    when (it) {
                        LocalAuthStatus.RefreshToken -> _next.value = LoginRoutes.WELCOME
                        is LocalAuthStatus.Success -> _next.value = HomeRoutes.START
                        LocalAuthStatus.UserCancelled -> _showUnlock.value = true
                        LocalAuthStatus.SecureStoreError,
                        LocalAuthStatus.BioCheckFailed -> {
                            // Allow user to make multiple fails... do nothing for now
                        }
                    }
                }
            )
        }
    }
}
