package uk.gov.onelogin.tokens.usecases

import javax.inject.Inject
import uk.gov.onelogin.login.usecase.extractPersistentIdFromIdToken
import uk.gov.onelogin.repositiories.TokenRepository

fun interface GetPersistentId {
    /**
     * Use case to get the persistent id from the id token
     *
     * @return persistent id as a string or null if it fails to retrieve it
     */
    operator fun invoke(): String?
}

class GetPersistentIdImpl @Inject constructor(
    val tokenRepository: TokenRepository
) : GetPersistentId {
    override fun invoke(): String? {
        val idToken: String = tokenRepository.getTokenResponse()?.idToken ?: return null
        val id = idToken.extractPersistentIdFromIdToken()
        return id
    }
}
