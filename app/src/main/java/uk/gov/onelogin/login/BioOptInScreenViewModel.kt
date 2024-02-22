package uk.gov.onelogin.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler

@HiltViewModel
class BioOptInScreenViewModel @Inject constructor(
    private val biometricPreferenceHandler: BiometricPreferenceHandler
) : ViewModel() {
    fun setBioPreference(pref: BiometricPreference) {
        biometricPreferenceHandler.setBioPref(pref)
    }
}
