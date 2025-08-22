package uk.gov.onelogin.features.wallet.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.android.wallet.core.wallet.WalletRoutes

@Composable
fun WalletScreen(
    setShowNavBarState: (Boolean) -> Unit,
    viewModel: WalletScreenViewModel = hiltViewModel()
) {
    viewModel.walletSdk.WalletApp(deeplink = viewModel.getCredential())
    setShowNavBarState(viewModel.walletSdk.shouldShowNavBar.collectAsStateWithLifecycle().value)
}
