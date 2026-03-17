package uk.gov.onelogin.features.wallet.domain

import uk.gov.android.wallet.sdk.WalletSdk
import javax.inject.Inject

fun interface WalletIsEmptyUseCase {
    fun invoke(): Boolean
}

class WalletIsEmptyUseCaseImpl
    @Inject
    constructor(
        private val walletSdk: WalletSdk,
    ) : WalletIsEmptyUseCase {
        override fun invoke(): Boolean = walletSdk.isEmpty()

        data class WalletIsEmptyDataError(
            override val message: String = WALLET_IS_NOT_EMPTY_DATA_ERROR,
        ) : Exception()

        data class CouldNotDetermineIfWalletIsEmpty(
            override val message: String? = WalletSdk.WalletSdkError.WalletEmptyCheckFailed().message
        ) : Exception()

        companion object {
            const val WALLET_IS_NOT_EMPTY_DATA_ERROR = "secure wallet data deleted"
        }
    }
