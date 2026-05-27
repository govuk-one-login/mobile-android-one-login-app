package uk.gov.onelogin.core.tokens.domain.retrieve

fun interface GetWalletStoreId {
    /**
     * Use case to get the Wallet Store ID from the open secure store
     *
     * @return wallet store id as a string or null if it fails to retrieve it
     */
    suspend operator fun invoke(): String?
}
