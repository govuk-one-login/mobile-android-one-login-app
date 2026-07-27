package uk.gov.onelogin.core.tokens.domain.idtoken.walletId

/**
 * Removes the Wallet Store ID from the open secure store.
 */
fun interface RemoveWalletStoreId {
    /**
     * Removes the wallet ID from storage.
     */
    suspend operator fun invoke(): Unit?
}
