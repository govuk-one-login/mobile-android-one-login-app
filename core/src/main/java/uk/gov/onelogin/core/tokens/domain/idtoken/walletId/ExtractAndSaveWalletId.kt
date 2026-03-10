package uk.gov.onelogin.core.tokens.domain.idtoken.walletId

/**
 * Verifies the id token body contains a wallet subject id claim and saved it to the open secure store
 */
fun interface ExtractAndSaveWalletId {
    /**
     * Verifies the id token contains a wallet subject id.
     * @param idToken - the id token returned from the token endpoint during login
     * @return [String] - nullable, if the return is null, the wallet subject id claim does not exist or an error
     * was thrown during the extraction
     */
    suspend fun extractAndSave(idToken: String): String?
}
