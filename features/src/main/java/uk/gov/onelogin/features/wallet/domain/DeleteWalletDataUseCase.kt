package uk.gov.onelogin.features.wallet.domain

import javax.inject.Inject
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.developer.ui.securestore.SecureStoreDevOptionsRepository

fun interface DeleteWalletDataUseCase : DelegateDeleteWalletUseCase {
    override suspend fun invoke(): Boolean
}

class DeleteWalletDataUseCaseImpl
@Inject
constructor(private val walletSdk: WalletSdk) :
    DeleteWalletDataUseCase {
    override suspend fun invoke(): Boolean = walletSdk.deleteWalletData()

    data class DeleteWalletDataError(override val message: String = DELETE_WALLET_DATA_ERROR) :
        Exception()

    companion object {
        const val DELETE_WALLET_DATA_ERROR = "Failed deleting wallet data."
    }
}

class DeleteWalletDataUseCaseDevOption(
    private val walletSdk: WalletSdk,
    private val secureStoreDevOptionsRepository: SecureStoreDevOptionsRepository,
) : DeleteWalletDataUseCase {
    override suspend fun invoke(): Boolean =
        if (secureStoreDevOptionsRepository.isWalletMockOverride()) {
            // this becomes the mock wallet sdk triggering its own delete function
            // showing the mock wallet delete UI
            walletSdk.deleteWalletData()
        } else if (secureStoreDevOptionsRepository.isWalletDeleteOverride()) {
            throw SecureStorageErrorV2(Exception("Simulate wallet deletion failure"))
        } else {
            walletSdk.deleteWalletData()
        }
}
