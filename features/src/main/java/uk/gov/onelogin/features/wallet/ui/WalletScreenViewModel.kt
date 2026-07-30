package uk.gov.onelogin.features.wallet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreId
import uk.gov.onelogin.core.ui.wallet.WalletAppDisplayer
import javax.inject.Inject

/**
 * ViewModel for [WalletScreen].
 *
 * Manages the lifecycle of the Wallet SDK initialisation and exposes [WalletScreenState] to
 * the UI.
 *
 * The Wallet SDK requires [WalletSdk.setWalletStoreId] to be called before [WalletSdk.WalletApp]
 * is invoked. This constraint is enforced structurally.
 *
 */
@HiltViewModel
class WalletScreenViewModel
    @Inject
    constructor(
        val walletSdk: WalletSdk,
        private val getWalletStoreId: GetWalletStoreId,
        private val walletAppDisplayer: WalletAppDisplayer,
    ) : ViewModel() {

    private val _state: MutableStateFlow<WalletScreenState> = MutableStateFlow(
        WalletScreenState.Loading
    )
    val state: StateFlow<WalletScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = WalletScreenState.Display(prepareWalletSdkForDisplay())
        }
    }

    /**
     * Retrieves the wallet store ID and sets it on the SDK before returning the Wallet Sdk for Initialisation.
     *
     */
    private suspend fun prepareWalletSdkForDisplay(): WalletAppDisplayer {
        val walletStoreId = getWalletStoreId()

        // Must be called before WalletSdk.WalletApp()
        walletStoreId?.let { walletSdk.setWalletStoreId(it) }

        return walletAppDisplayer
    }
}
