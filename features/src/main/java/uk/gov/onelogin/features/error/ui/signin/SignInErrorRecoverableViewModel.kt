package uk.gov.onelogin.features.error.ui.signin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import javax.inject.Inject

@HiltViewModel
class SignInErrorRecoverableViewModel
    @Inject
    constructor(
        private val navigator: Navigator,
    ) : ViewModel() {
        fun onBack() {
            navigator.navigate(LoginRoutes.Welcome, true)
        }

        fun onClick() {
            navigator.navigate(LoginRoutes.Start, true)
        }
    }
