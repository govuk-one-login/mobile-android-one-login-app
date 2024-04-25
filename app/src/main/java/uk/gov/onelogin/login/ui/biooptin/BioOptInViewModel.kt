package uk.gov.onelogin.login.ui.biooptin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore

@HiltViewModel
class BioOptInViewModel @Inject constructor(
    private val biometricPreferenceHandler: BiometricPreferenceHandler,
    private val autoInitialiseSecureStore: AutoInitialiseSecureStore
) : ViewModel() {
    fun setBioPreference(pref: BiometricPreference) {
        biometricPreferenceHandler.setBioPref(pref)
        autoInitialiseSecureStore()
    }
}
