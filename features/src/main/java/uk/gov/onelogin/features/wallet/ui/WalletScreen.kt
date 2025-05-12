package uk.gov.onelogin.features.wallet.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WalletScreen(viewModel: WalletScreenViewModel = hiltViewModel()) {
    viewModel.walletSdk.WalletApp(deeplink = viewModel.getCredential(), adminEnabled = false)
}
