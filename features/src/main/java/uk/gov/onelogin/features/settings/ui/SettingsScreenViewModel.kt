package uk.gov.onelogin.features.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.onelogin.core.navigation.data.SettingsRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.retrieve.GetEmail
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.optin.data.OptInRepository

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val optInRepository: OptInRepository,
    private val navigator: Navigator,
    private val localAuthManager: LocalAuthManager,
    featureFlags: FeatureFlags,
    tokenRepository: TokenRepository,
    getEmail: GetEmail
) : ViewModel() {
    private val _optInState = MutableStateFlow(false)
    val optInState: StateFlow<Boolean>
        get() = _optInState.asStateFlow()
    private val _biometricsOptionState = MutableStateFlow(false)
    val biometricsOptionState: StateFlow<Boolean>
        get() = _biometricsOptionState.asStateFlow()

    val isWalletEnabled = featureFlags[WalletFeatureFlag.ENABLED]

    init {
        viewModelScope.launch {
            optInRepository.hasAnalyticsOptIn().collect {
                _optInState.emit(it)
            }
        }
    }

    val email = getEmail(tokenRepository.getTokenResponse()?.idToken ?: "").orEmpty()

    fun checkDeviceBiometricsStatus() {
        viewModelScope.launch {
            _biometricsOptionState.emit(localAuthManager.biometricsAvailable())
        }
    }

    fun goToSignOut() {
        navigator.navigate(SignOutRoutes.Start)
    }

    fun goToOssl() {
        navigator.navigate(SettingsRoutes.Ossl)
    }

    fun goToBiometricsOptIn() {
        navigator.navigate(SettingsRoutes.BiometricsOptIn)
    }

    fun toggleOptInPreference() {
        viewModelScope.launch {
            _optInState.update { currentOptInState ->
                currentOptInState.not().also { newOptInState ->
                    when (newOptInState) {
                        true -> optInRepository.optIn()
                        false -> optInRepository.optOut()
                    }
                }
            }
        }
    }
}
