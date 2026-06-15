package uk.gov.onelogin.core.tokens.domain.idtoken.walletId

/**
 * Removes the wallet ID from the open secure store.
 */
fun interface RemoveWalletId {
    /**
     * Removes the wallet ID from storage.
     */
    suspend fun removeWalletId()
}
