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
import uk.gov.onelogin.features.login.domain.validateWalletStoreId.ValidateWalletStoreId
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
     * Retrieves the wallet store ID and configures the Wallet SDK before display.
     *
     * Crashes with [IllegalArgumentException] if the wallet store ID is null. This should never
     * happen in normal operation — a null value indicates that [ValidateWalletStoreId] failed to
     * gate the user's session correctly. The crash is intentional, giving [ValidateWalletStoreId]
     * another opportunity to run on the next app start.
     *
     * Must be called before [WalletSdk.WalletApp] is invoked.
     *
     * @return the [WalletAppDisplayer] ready to render the Wallet SDK.
     * @throws IllegalArgumentException if the wallet store ID is null.
     */
    private suspend fun prepareWalletSdkForDisplay(): WalletAppDisplayer {
        val walletStoreId = getWalletStoreId()

        requireNotNull(walletStoreId) {
            "Wallet Store ID must not be null. This is guaranteed by ValidateWalletStoreId"
        }

        // Must be called before WalletSdk.WalletApp()
        walletSdk.setWalletStoreId(walletStoreId)

        return walletAppDisplayer
    }
}
