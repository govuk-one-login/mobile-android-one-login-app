package uk.gov.onelogin.core.tokens.domain.retrieve

fun interface GetWalletStoreId {
    /**
     * Use case to retrieve the Wallet Store ID.
     *
     * The Wallet Store ID is an ID that links wallet credentials to a particular user.
     *
     * It distinguishes the underlying One Login account and doesn't change over time, allowing wallet SDK to segregate and encrypt data on the device for a given user.
     *
     * @return wallet store id as a string or null if it fails to retrieve it
     */
    suspend operator fun invoke(): String?
}
