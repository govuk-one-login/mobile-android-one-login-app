package uk.gov.onelogin.ui.error.reauth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.navigation.Navigator

@HiltViewModel
class ReAuthErrorViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel() {
    fun navigateToSignIn() {
        navigator.navigate(LoginRoutes.Start, true)
    }
}
