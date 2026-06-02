package uk.gov.onelogin.core.tokens.domain.retrieve

fun interface GetWalletStoreId {
    /**
     * Use case to retrieve the Wallet Store ID.
     *
     * The Wallet Store ID is an identifier for associating wallet database instances with users
     * and removes the need to delete credentials.
     *
     * @return wallet store id as a string or null if it fails to retrieve it
     */
    suspend operator fun invoke(): String?
}
