package uk.gov.onelogin.tokens.usecases

import javax.inject.Inject
import uk.gov.onelogin.login.usecase.extractEmailFromIdToken
import uk.gov.onelogin.repositiories.TokenRepository

fun interface GetEmail {
    /**
     * Use case to get the email from the id token
     *
     * @return email as a string or null if it fails to retrieve it
     */
    operator fun invoke(): String?
}

class GetEmailImpl @Inject constructor(
    val tokenRepository: TokenRepository
) : GetEmail {
    override fun invoke(): String? {
        val idToken: String = tokenRepository.getTokenResponse()?.idToken ?: return null
        val email = idToken.extractEmailFromIdToken()
        return email
    }
}
