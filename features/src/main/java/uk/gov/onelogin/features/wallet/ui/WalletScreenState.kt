package uk.gov.onelogin.features.wallet.ui

import uk.gov.onelogin.core.ui.wallet.WalletDisplayer

/**
 * The sealed class is  the UI state of [WalletScreen].
 *
 *
 * - [Loading] is the initial state. It acts as a structural guard to ensure the Wallet SDK
 *   is never rendered before [WalletDisplayer]
 *
 * - [Display] is set after the wallet store ID has been retrieved and supplied to the SDK.
 *   The screen remains in this state for all subsequent navigations.
 */
sealed interface WalletScreenState {


    data object Loading : WalletScreenState

    data class Display(
        val walletDisplayer: WalletDisplayer,
    ) : WalletScreenState
}
