package uk.gov.onelogin.features.wallet.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.wallet.data.WalletRepository

@HiltViewModel
class WalletScreenViewModel @Inject constructor(
    val walletSdk: WalletSdk,
    private val walletRepository: WalletRepository
) : ViewModel() {

    init {
        checkWalletEnabled()
    }

    fun checkWalletEnabled() {
        if (walletRepository.isWalletDeepLinkPath()) {
            walletRepository.toggleWallDeepLinkPathState()
        }
    }
}
