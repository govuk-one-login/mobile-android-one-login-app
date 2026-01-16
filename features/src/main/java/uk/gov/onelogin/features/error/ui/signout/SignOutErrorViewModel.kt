package uk.gov.onelogin.features.error.ui.signout

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.onelogin.core.navigation.domain.Navigator
import javax.inject.Inject

@HiltViewModel
class SignOutErrorViewModel
    @Inject
    constructor(
        val navigator: Navigator,
    ) : ViewModel() {
        fun goBackToSettingsScreen() {
            navigator.goBack()
            navigator.goBack()
        }
    }
