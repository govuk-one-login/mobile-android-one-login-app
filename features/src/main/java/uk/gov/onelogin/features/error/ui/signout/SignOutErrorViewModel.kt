package uk.gov.onelogin.features.error.ui.signout

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.core.navigation.domain.Navigator

@HiltViewModel
class SignOutErrorViewModel @Inject constructor(
    val navigator: Navigator
) : ViewModel() {
    fun goBackToSettingsScreen() {
        navigator.goBack()
        navigator.goBack()
    }
}
