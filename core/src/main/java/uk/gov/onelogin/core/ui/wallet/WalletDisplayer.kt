package uk.gov.onelogin.core.ui.wallet

import androidx.compose.runtime.Composable

/**
 * An interface for initialising the wallet sdk
 */
fun interface WalletDisplayer {

    @Composable
    fun Wallet(
        onFullScreenRequest: (Boolean) -> Unit
    )
}


