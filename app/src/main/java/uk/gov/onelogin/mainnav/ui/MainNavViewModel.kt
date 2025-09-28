package uk.gov.onelogin.mainnav.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.wallet.data.WalletRepository

@HiltViewModel
class MainNavViewModel @Inject constructor(
    private val features: FeatureFlags,
    private val walletRepository: WalletRepository
) : ViewModel() {
    private val _walletEnabled = mutableStateOf(false)
    val walletEnabled: State<Boolean> = _walletEnabled

    private val _displayContentAsFullScreenState = mutableStateOf(false)
    val displayContentAsFullScreenState: State<Boolean> = _displayContentAsFullScreenState

    fun setDisplayContentAsFullScreenState(newValue: Boolean) {
        _displayContentAsFullScreenState.value = newValue
    }

    private val _isDeeplinkRoute = MutableStateFlow(false)
    val isDeeplinkRoute: StateFlow<Boolean> = _isDeeplinkRoute

    init {
        // Check if wallet is enabled and user comes in via deeplink
        checkWalletEnabled()
    }

    fun checkWalletEnabled() {
        _walletEnabled.value = features[WalletFeatureFlag.ENABLED]
        _walletEnabled.value = features[WalletFeatureFlag.ENABLED]
        _isDeeplinkRoute.value = walletRepository.getWalletDeepLinkPathState() &&
            walletEnabled.value
    }
}
