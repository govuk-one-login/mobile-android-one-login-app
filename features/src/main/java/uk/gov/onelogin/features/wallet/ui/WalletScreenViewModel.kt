package uk.gov.onelogin.features.wallet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreId
import uk.gov.onelogin.core.tokens.utils.MainDispatcher
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
        @MainDispatcher private val dispatcher: CoroutineDispatcher
    ) : ViewModel() {

    private val _state: MutableStateFlow<WalletScreenState> = MutableStateFlow(
        WalletScreenState.Loading
    )
    val state: StateFlow<WalletScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            _state.value = WalletScreenState.Display(prepareWalletSdkForDisplay())
        }
    }

    /**
     * Retrieves the wallet store ID and sets it on the SDK before returning the displayer.
     *
     * Order is significant:
     * - [WalletSdk.setWalletStoreId] must be called before [WalletAppDisplayer.WalletApp]
     *   is composed.
     */
    private suspend fun prepareWalletSdkForDisplay(): WalletAppDisplayer {
        val walletStoreId = getWalletStoreId()

        // Must be called before WalletSdk.WalletApp()
        walletStoreId?.let { walletSdk.setWalletStoreId(it) }

        return walletAppDisplayer
    }
}
