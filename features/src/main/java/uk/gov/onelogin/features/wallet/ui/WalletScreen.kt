package uk.gov.onelogin.features.wallet.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WalletScreen(
    viewModel: WalletScreenViewModel = hiltViewModel()
) {
    viewModel.walletSdk.WalletApp(deeplink = "", adminEnabled = false)
}
