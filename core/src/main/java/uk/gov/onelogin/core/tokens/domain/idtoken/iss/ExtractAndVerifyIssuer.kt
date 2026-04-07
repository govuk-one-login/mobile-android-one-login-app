package uk.gov.onelogin.core.tokens.domain.idtoken.iss

/**
 * Verifies the id token body contains an iss claim and the value is as expected.
 */
fun interface ExtractAndVerifyIssuer {
    /**
     * Verifies the id token iss claim is valid.
     * @param idToken - the id token returned from the token endpoint during login
     * @return [Boolean] - true if is valid, and false is the value is invalid or an error occurred
     * during the extraction/ verification.
     */
    fun verify(idToken: String): Boolean
}
