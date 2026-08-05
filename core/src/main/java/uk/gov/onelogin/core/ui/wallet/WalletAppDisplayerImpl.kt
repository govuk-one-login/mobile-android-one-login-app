package uk.gov.onelogin.core.ui.wallet

import androidx.compose.runtime.Composable
import uk.gov.android.wallet.sdk.WalletSdk
import javax.inject.Inject

class WalletAppDisplayerImpl @Inject constructor(
    private val walletSdk: WalletSdk
) : WalletAppDisplayer {

    @Composable
    override fun WalletApp() {
        walletSdk.WalletApp()
    }
}
