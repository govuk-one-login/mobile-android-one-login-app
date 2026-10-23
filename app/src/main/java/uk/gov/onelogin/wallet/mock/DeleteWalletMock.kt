package uk.gov.onelogin.wallet.mock

fun interface DeleteMockWalletDataUseCase {
    suspend fun invoke(): Boolean
}

// I think I should wrap this alongside the DeleteWalletDataUseCase interface
// to make it easier to test. to switch between the real and mock implementations,
// or I can leave the delete function accessible to be called when we have an actual
// test scenario.
// the choice is wrap the mock alongside the real implementation so when we test
// the SignOut scenario it uses a mock as well as the real implementation.
// the WalletMockTest calls the deleteWalletData directly at the moment

// we are considering another wrapper for the DeleteWalletModule that takes in
// DeleteWalletDataUseCase and returns a DeleteWalletDataUseCase

class DeleteWalletMock(private val walletMock: WalletMock) : DeleteMockWalletDataUseCase {

    override suspend fun invoke(): Boolean = walletMock.deleteWalletData()

    data class DeleteWalletDataError(override val message: String = DELETE_WALLET_DATA_ERROR) :
        Exception()

    companion object {
        const val DELETE_WALLET_DATA_ERROR = "Failed deleting wallet data."
    }
}
