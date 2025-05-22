package uk.gov.onelogin.features.settings.ui.biometricsoptin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.preference.LocalAuthPreference
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

@HiltViewModel
class BiometricsOptInScreenViewModel @Inject constructor(
    private val featureFlags: FeatureFlags,
    private val localAuthManager: LocalAuthManager,
    private val navigator: Navigator
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
        localAuthManager.toggleBiometrics()
        _biometricsEnabled.value = checkBiometricsEnabledState()
    }

    private fun checkBiometricsEnabledState() =
        localAuthManager.localAuthPreference == LocalAuthPreference.Enabled(true)
}
