package uk.gov.onelogin.features.wallet.domain

import uk.gov.android.wallet.sdk.WalletSdk

fun interface DeleteMockWalletDataUseCase {
    suspend fun invoke(): Boolean
}

/**
 * Mock implementation of [DeleteMockWalletDataUseCase] that delegates to [WalletSdk]
 * and throws [DeleteWalletMockError] to signal navigation to the mock wallet UI.
 */
class DeleteWalletMock(private val walletSdk: WalletSdk) : DeleteMockWalletDataUseCase {

    override suspend fun invoke(): Boolean {
        walletSdk.deleteWalletData()
        throw DeleteWalletMockError()
    }

    class DeleteWalletMockError : Exception("Mock wallet deletion triggered navigation to mock UI")
}
