package uk.gov.onelogin.core.tokens.domain.retrieve

import javax.inject.Inject
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken

class GetEmailImpl @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val verifyIdToken: VerifyIdToken
) : GetEmail {
    override fun invoke(): String? {
        val idToken: String = tokenRepository.getTokenResponse()?.idToken ?: return null
        val email = verifyIdToken.extractEmailFromIdToken(idToken)
        return email
    }
}
