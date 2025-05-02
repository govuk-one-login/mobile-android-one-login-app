package uk.gov.onelogin.features.wallet.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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
    private val _walletEnabled = mutableStateOf(false)
    val walletEnabled: State<Boolean> = _walletEnabled

    private val _walletDeeplink = mutableStateOf("")
    val walletDeeplink: State<String> = _walletDeeplink

    private val _walletDeepLinkReceived = mutableStateOf(false)
    val walletDeepLinkReceived: State<Boolean> = _walletDeepLinkReceived

    fun checkWalletEnabled() {
        _walletEnabled.value = features[WalletFeatureFlag.ENABLED]
        _walletDeeplink.value = walletRepository.getCredential()
        _walletDeepLinkReceived.value = walletRepository.getDeepLinkPath() == DEEPLINK_PATH
    }

    fun getCredential(): String {
        return walletRepository.getCredential()
    }
}

const val DEEPLINK_PATH = "/wallet/add"
