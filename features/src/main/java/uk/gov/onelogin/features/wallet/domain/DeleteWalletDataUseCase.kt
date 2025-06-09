package uk.gov.onelogin.features.wallet.domain

import javax.inject.Inject
import uk.gov.android.wallet.sdk.WalletSdk

fun interface DeleteWalletDataUseCase {
    suspend fun invoke()
}

class DeleteWalletDataUseCaseImpl @Inject constructor(
    private val walletSdk: WalletSdk
) : DeleteWalletDataUseCase {
    override suspend fun invoke() {
        val deleteResult = walletSdk.deleteWalletData()
        if (!deleteResult) throw DeleteWalletDataError()
    }

    data class DeleteWalletDataError(
        override val message: String = DELETE_WALLET_DATA_ERROR
    ) : Exception()

    companion object {
        const val DELETE_WALLET_DATA_ERROR = "Failed deleting wallet data."
    }
}
