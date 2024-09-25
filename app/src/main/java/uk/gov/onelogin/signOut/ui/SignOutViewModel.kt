package uk.gov.onelogin.signOut.ui

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.signOut.domain.SignOutError
import uk.gov.onelogin.signOut.domain.SignOutUseCase
import uk.gov.onelogin.ui.error.ErrorRoutes

@HiltViewModel
class SignOutViewModel @Inject constructor(
    private val navigator: Navigator,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {
    fun signOut(activityFragment: FragmentActivity) {
        viewModelScope.launch {
            try {
                signOutUseCase.invoke(activityFragment)
                navigator.navigate(LoginRoutes.Root, true)
            } catch (e: SignOutError) {
                Log.e(this::class.simpleName, e.message, e)
                navigator.navigate(ErrorRoutes.SignOut)
            }
        }
    }

    fun goBack() {
        navigator.goBack()
    }
}
