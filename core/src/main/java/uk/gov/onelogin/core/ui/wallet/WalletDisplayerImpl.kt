package uk.gov.onelogin.core.ui.wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import javax.inject.Inject
import uk.gov.android.wallet.sdk.WalletSdk

class WalletDisplayerImpl @Inject constructor(private val walletSdk: WalletSdk) : WalletDisplayer {

    @Composable
    override fun Wallet(onFullScreenRequest: (Boolean) -> Unit) {
        val fullScreen by walletSdk
            .displayAsFullScreen
            .collectAsStateWithLifecycle()

        LaunchedEffect(fullScreen) {
            onFullScreenRequest(fullScreen)
        }

        walletSdk.WalletApp()
    }
}
