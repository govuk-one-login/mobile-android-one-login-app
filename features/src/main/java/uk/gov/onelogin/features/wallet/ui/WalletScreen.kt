package uk.gov.onelogin.features.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreen
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreenAnalyticsViewModel

@Composable
fun WalletScreen(
    deepLinkRoute: Boolean,
    setDisplayContentAsFullScreen: (Boolean) -> Unit,
    viewModel: WalletScreenViewModel = hiltViewModel(),
    loadingAnalyticsViewModel: LoadingScreenAnalyticsViewModel = hiltViewModel(),
) {


    key(deepLinkRoute) {
        when (val state = viewModel.state.collectAsState().value) {
            is WalletScreenState.Loading -> {
                LoadingScreen(analyticsViewModel = loadingAnalyticsViewModel) { }
            }
            is WalletScreenState.Display ->
                state.walletAppDisplayer.WalletApp()
        }
    }

    setDisplayContentAsFullScreen(
        viewModel.walletSdk.displayAsFullScreen
            .collectAsStateWithLifecycle()
            .value,
    )
}
