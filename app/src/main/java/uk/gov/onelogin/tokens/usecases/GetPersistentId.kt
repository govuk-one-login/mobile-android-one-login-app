package uk.gov.onelogin.tokens.usecases

import javax.inject.Inject
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
    val getFromOpenSecureStore: GetFromOpenSecureStore
) : GetPersistentId {
    override suspend fun invoke(): String? {
        return getFromOpenSecureStore(Keys.PERSISTENT_ID_KEY)?.get(Keys.PERSISTENT_ID_KEY)
    }
}
