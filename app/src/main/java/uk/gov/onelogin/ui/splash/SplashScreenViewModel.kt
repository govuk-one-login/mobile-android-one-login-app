package uk.gov.onelogin.ui.splash

import android.util.Log
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
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.android.securestore.error.SecureStoreErrorType
import uk.gov.onelogin.login.LoginRoutes
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
            try {
                if (handleLogin(fragmentActivity)) {
                    _next.value = HomeRoutes.START
                } else {
                    _next.value = LoginRoutes.WELCOME
                }
            } catch (e: SecureStorageError) {
                Log.e(this::class.simpleName, e.message, e)
                if (e.type == SecureStoreErrorType.USER_CANCELED_BIO_PROMPT) {
                    _showUnlock.value = true
                } else {
                    _next.value = LoginRoutes.WELCOME
                }
            }
        }
    }
}
