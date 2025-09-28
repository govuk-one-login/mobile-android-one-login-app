package uk.gov.onelogin.features.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WalletScreen(
    deepLinkRoute: Boolean,
    setDisplayContentAsFullScreen: (Boolean) -> Unit,
    viewModel: WalletScreenViewModel = hiltViewModel()
) {
    key(deepLinkRoute) {
        viewModel.walletSdk.WalletApp()
    }

    setDisplayContentAsFullScreen(
        viewModel.walletSdk.displayAsFullScreen.collectAsStateWithLifecycle().value
    )
}
