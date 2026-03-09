package uk.gov.onelogin.core.tokens.domain.idtoken.email

fun interface ExtractEmail {
    /**
     * Use case to get the email from the id token
     *
     * @return email as a string or null if it fails to retrieve it
     */
    operator fun invoke(idToken: String): String?
}
