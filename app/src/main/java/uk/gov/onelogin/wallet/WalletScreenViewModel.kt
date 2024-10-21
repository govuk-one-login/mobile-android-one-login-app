package uk.gov.onelogin.wallet

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.android.features.FeatureFlags
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.WalletFeatureFlag

@HiltViewModel
class WalletScreenViewModel @Inject constructor(
    val walletSdk: WalletSdk,
    private val features: FeatureFlags
) : ViewModel() {
    private val _enabled = mutableStateOf(false)
    val enabled: State<Boolean> = _enabled

    fun isWalletEnabled() {
        _enabled.value = features[WalletFeatureFlag.ENABLED]
    }
}
