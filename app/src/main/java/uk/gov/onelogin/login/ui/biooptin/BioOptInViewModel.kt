package uk.gov.onelogin.login.ui.biooptin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.mainnav.MainNavRoutes
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore

@HiltViewModel
class BioOptInViewModel @Inject constructor(
    private val biometricPreferenceHandler: BiometricPreferenceHandler,
    private val autoInitialiseSecureStore: AutoInitialiseSecureStore,
    private val navigator: Navigator
) : ViewModel() {

    fun useBiometrics() {
        setBioPreference(BiometricPreference.BIOMETRICS)
        goToHome()
    }

    fun doNotUseBiometrics() {
        setBioPreference(BiometricPreference.PASSCODE)
        goToHome()
    }

    private fun setBioPreference(pref: BiometricPreference) {
        biometricPreferenceHandler.setBioPref(pref)
        viewModelScope.launch {
            autoInitialiseSecureStore.initialise()
        }
    }

    private fun goToHome() {
        navigator.navigate(MainNavRoutes.Start, true)
    }
}
