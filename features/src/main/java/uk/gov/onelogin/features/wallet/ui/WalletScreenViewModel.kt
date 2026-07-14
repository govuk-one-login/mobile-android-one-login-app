package uk.gov.onelogin.features.wallet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.logging.api.LogTagProvider
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreId
import javax.inject.Inject

@HiltViewModel
class WalletScreenViewModel
    @Inject
    constructor(
        val walletSdk: WalletSdk,
        private val getWalletStoreId: GetWalletStoreId,
        private val logger: Logger
    ) : ViewModel(),
        LogTagProvider {
        fun setWalletStoreId() {
            viewModelScope.launch {
                runCatching { getWalletStoreId() }
                    .onSuccess { storeId -> storeId?.let {
                        walletSdk.setWalletStoreId(it) } }
                    .onFailure { logError(it) }

            }
        }

        private fun logError(throwable: Throwable) {
            logger.error(
                message = COULD_NOT_SET_WALLET_STORE_ID,
                throwable = SetWalletStoreIdException(throwable),
                actionKey(ACTION),
                componentKey(COMPONENT),
            )
        }

        internal class SetWalletStoreIdException(
            cause: Throwable
        ) : RuntimeException(COULD_NOT_SET_WALLET_STORE_ID, cause)

        companion object {
            private const val COMPONENT = "wallet_screen_view_model"
            private const val ACTION = "set wallet store ID"
            private const val COULD_NOT_SET_WALLET_STORE_ID = "Could not set Wallet store ID"
        }
    }
