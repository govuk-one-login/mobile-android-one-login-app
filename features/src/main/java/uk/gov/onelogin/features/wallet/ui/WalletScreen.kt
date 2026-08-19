package uk.gov.onelogin.features.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreen
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreenAnalyticsViewModel

@Composable
fun WalletScreen(
    deepLinkRoute: Boolean,
    setDisplayContentAsFullScreen: (Boolean) -> Unit,
    viewModel: WalletScreenViewModel = hiltViewModel(),
    loadingAnalyticsViewModel: LoadingScreenAnalyticsViewModel = hiltViewModel(),
) {

    val state: WalletScreenState by viewModel.state.collectAsState()

    key(deepLinkRoute) {
        when (val state = state) {
            is WalletScreenState.Loading -> {
                LoadingScreen(analyticsViewModel = loadingAnalyticsViewModel) { }
            }
            is WalletScreenState.Display -> {
                state.walletDisplayer.Wallet(
                    onFullScreenRequest = setDisplayContentAsFullScreen
                )
            }
        }
    }
}
