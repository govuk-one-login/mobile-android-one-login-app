package uk.gov.onelogin.features.error.ui.appreset

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator

@HiltViewModel
class AppResetErrorViewModel
@Inject
constructor(private val navigator: Navigator,) : ViewModel() {
    fun navigateToSignIn() {
        navigator.navigate(LoginRoutes.Start, true)
    }
}
