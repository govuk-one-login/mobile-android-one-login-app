package uk.gov.onelogin.wallet.mock


fun interface DeleteWalletDataUseCase {
    suspend fun invoke(): Boolean
}

class DeleteWalletMock (private  val walletMock: WalletMock): DeleteWalletDataUseCase {

    override suspend fun invoke(): Boolean = walletMock.deleteWalletData()

    data class DeleteWalletDataError(
        override val message: String = DELETE_WALLET_DATA_ERROR,
    ) : Exception()

    companion object {
        const val DELETE_WALLET_DATA_ERROR = "Failed deleting wallet data."
    }

}
