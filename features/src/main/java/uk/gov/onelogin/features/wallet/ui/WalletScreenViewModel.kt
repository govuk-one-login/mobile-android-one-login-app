package uk.gov.onelogin.features.wallet.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.wallet.data.WalletRepository

@HiltViewModel
class WalletScreenViewModel @Inject constructor(
    val walletSdk: WalletSdk,
    private val features: FeatureFlags,
    private val walletRepository: WalletRepository
) : ViewModel() {
    private val _walletEnabled = MutableStateFlow(false)
    val walletEnabled: StateFlow<Boolean> = _walletEnabled

    private val _isDeeplinkRoute = MutableStateFlow(false)
    val isDeeplinkRoute: StateFlow<Boolean> = _isDeeplinkRoute

    init {
        checkWalletEnabled()
    }

    fun checkWalletEnabled() {
        _walletEnabled.value = features[WalletFeatureFlag.ENABLED]
        _isDeeplinkRoute.value = walletRepository.getWalletDeepLinkPathState() &&
            walletEnabled.value
        if (walletRepository.getWalletDeepLinkPathState()) {
            walletRepository.toggleWallDeepLinkPathState()
        }
    }
}
