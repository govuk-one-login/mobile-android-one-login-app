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
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

@HiltViewModel
class BiometricsToggleScreenViewModel @Inject constructor(
    private val featureFlags: FeatureFlags,
    private val localAuthManager: LocalAuthManager,
    private val navigator: Navigator,
    private val saveTokens: SaveTokens
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
        // only if the initial local auth preference is disabled
        if (localAuthManager.localAuthPreference is LocalAuthPreference.Disabled) {
            // then we save the tokens in memory
            // because the toggle has already been pressed AND this should never show unless the
            // biometrics are available as we do a check when landing on teh screen (see checkBiometricsAvailable() call point)
            viewModelScope.launch {
                saveTokens()
            }
        }
        // toggle the preference
        localAuthManager.toggleBiometrics()
        _biometricsEnabled.value = checkBiometricsEnabledState()
    }

    private fun checkBiometricsEnabledState() =
        localAuthManager.localAuthPreference == LocalAuthPreference.Enabled(true)
}
