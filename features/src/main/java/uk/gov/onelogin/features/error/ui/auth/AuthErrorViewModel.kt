package uk.gov.onelogin.features.error.ui.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import javax.inject.Inject

@HiltViewModel
class AuthErrorViewModel
    @Inject
    constructor(
        private val navigator: Navigator,
    ) : ViewModel() {
        fun navigateToSignIn() {
            navigator.navigate(LoginRoutes.Start, true)
        }
    }
