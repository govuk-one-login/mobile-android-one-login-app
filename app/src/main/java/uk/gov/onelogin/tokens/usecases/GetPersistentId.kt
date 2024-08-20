package uk.gov.onelogin.tokens.usecases

import javax.inject.Inject
import uk.gov.onelogin.login.usecase.extractPersistentIdFromIdToken
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys

fun interface GetPersistentId {
    /**
     * Use case to get the persistent id from the id token, failing that
     * the open secure store is checked
     *
     * @return persistent id as a string or null if it fails to retrieve it
     */
    suspend operator fun invoke(): String?
}

class GetPersistentIdImpl @Inject constructor(
    val tokenRepository: TokenRepository,
    val getFromOpenSecureStore: GetFromOpenSecureStore
) : GetPersistentId {
    override suspend fun invoke(): String? {
        var persistentId = tokenRepository.getTokenResponse()
            ?.idToken?.extractPersistentIdFromIdToken()
        if (persistentId == null) {
            persistentId = getFromOpenSecureStore(Keys.PERSISTENT_ID_KEY)
        }
        return persistentId
    }
}
