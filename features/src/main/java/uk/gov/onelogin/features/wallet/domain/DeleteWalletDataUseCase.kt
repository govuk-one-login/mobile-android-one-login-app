package uk.gov.onelogin.features.wallet.domain

import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.developer.ui.securestore.SecureStoreDevOptionsRepository
import javax.inject.Inject

fun interface DeleteWalletDataUseCase {
    suspend fun invoke(): Boolean
}

class DeleteWalletDataUseCaseImpl
    @Inject
    constructor(
        private val walletSdk: WalletSdk,
    ) : DeleteWalletDataUseCase {
        override suspend fun invoke(): Boolean = walletSdk.deleteWalletData()

        data class DeleteWalletDataError(
            override val message: String = DELETE_WALLET_DATA_ERROR,
        ) : Exception()

        companion object {
            const val DELETE_WALLET_DATA_ERROR = "Failed deleting wallet data."
        }
    }

class DeleteWalletDataUseCaseDevOption(
    private val walletSdk: WalletSdk,
    private val secureStoreDevOptionsRepository: SecureStoreDevOptionsRepository,
) : DeleteWalletDataUseCase {
    override suspend fun invoke(): Boolean {
        if (secureStoreDevOptionsRepository.isWalletDeleteOverride()) {
            throw SecureStorageError(Exception("Simulate wallet deletion failure"))
        } else {
            return walletSdk.deleteWalletData()
        }
    }
}
