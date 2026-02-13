package uk.gov.onelogin.mainnav.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uk.gov.onelogin.features.wallet.data.WalletRepository
import javax.inject.Inject

@HiltViewModel
class MainNavViewModel
    @Inject
    constructor(
        private val walletRepository: WalletRepository,
    ) : ViewModel() {
        private val _displayContentAsFullScreenState = mutableStateOf(false)
        val displayContentAsFullScreenState: State<Boolean> = _displayContentAsFullScreenState

        fun setDisplayContentAsFullScreenState(newValue: Boolean) {
            _displayContentAsFullScreenState.value = newValue
        }

        private val _isDeeplinkRoute = MutableStateFlow(false)
        val isDeeplinkRoute: StateFlow<Boolean> = _isDeeplinkRoute

        init {
            // Check if wallet is enabled and user comes in via deeplink
            checkIsDeeplinkRoute()
        }

        fun checkIsDeeplinkRoute() {
            _isDeeplinkRoute.value = walletRepository.isWalletDeepLinkPath()
        }
    }
