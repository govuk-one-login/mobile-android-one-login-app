package uk.gov.onelogin.features.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle

@Composable
fun WalletScreen(
    deepLinkRoute: Boolean,
    setDisplayContentAsFullScreen: (Boolean) -> Unit,
    viewModel: WalletScreenViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.setWalletStoreId()
        }
    }

    key(deepLinkRoute) {
        viewModel.walletSdk.WalletApp()
    }

    setDisplayContentAsFullScreen(
        viewModel.walletSdk.displayAsFullScreen
            .collectAsStateWithLifecycle()
            .value,
    )
}
