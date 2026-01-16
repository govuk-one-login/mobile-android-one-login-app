package uk.gov.onelogin.features.signout.ui.success

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import javax.inject.Inject

@HiltViewModel
class SignOutSuccessViewModel
    @Inject
    constructor(
        private val navigator: Navigator,
    ) : ViewModel() {
        fun navigateStart() {
            navigator.navigate(LoginRoutes.Root, true)
        }
    }
