package uk.gov.onelogin.features.wallet.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WalletScreen(
    displayContentAsFullScreen: (Boolean) -> Unit,
    viewModel: WalletScreenViewModel = hiltViewModel()
) {
    viewModel.walletSdk.WalletApp(deeplink = viewModel.getCredential())
    displayContentAsFullScreen(
        viewModel.walletSdk.displayAsFullScreen.collectAsStateWithLifecycle().value
    )
}
