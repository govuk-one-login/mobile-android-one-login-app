package uk.gov.onelogin.features.settings.ui.biometricstoggle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

@HiltViewModel
class BiometricsToggleScreenViewModel @Inject constructor(
    private val featureFlags: FeatureFlags,
    private val localAuthManager: LocalAuthManager,
    private val navigator: Navigator,
    private val autoInitialiseSecureStore: AutoInitialiseSecureStore
) : ViewModel() {
    private val _biometricsEnabled = MutableStateFlow(checkBiometricsEnabledState())
    val biometricsEnabled: StateFlow<Boolean> = _biometricsEnabled.asStateFlow()
    val walletEnabled: Boolean
        get() = featureFlags[WalletFeatureFlag.ENABLED]

    fun goBack() {
        navigator.goBack()
    }

    fun checkBiometricsAvailable() {
        if (!localAuthManager.biometricsAvailable()) {
            navigator.goBack()
        }
    }

    fun toggleBiometrics() {
        viewModelScope.launch {
            localAuthManager.toggleBiometrics()
            _biometricsEnabled.value = checkBiometricsEnabledState()
            if (_biometricsEnabled.value) {
                // Auto-initialising the secure store each time does not create any issues/ side effects
                // This includes saving the tokens as well
                autoInitialiseSecureStore.initialise()
            }
        }
    }

    private fun checkBiometricsEnabledState() =
        localAuthManager.localAuthPreference == LocalAuthPreference.Enabled(true)
}
