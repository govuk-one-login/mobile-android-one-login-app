package uk.gov.onelogin.features.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreen

@Composable
fun WalletScreen(
    deepLinkRoute: Boolean,
    setDisplayContentAsFullScreen: (Boolean) -> Unit,
    viewModel: WalletScreenViewModel = hiltViewModel(),
) {


    key(deepLinkRoute) {
        when (val state = viewModel.state.collectAsState().value) {
            is WalletScreenState.Loading -> {
                LoadingScreen { }
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
