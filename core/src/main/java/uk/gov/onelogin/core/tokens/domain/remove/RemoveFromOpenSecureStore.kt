package uk.gov.onelogin.core.tokens.domain.remove

fun interface RemoveFromOpenSecureStore {
    /**
     * Use case for removing data from the open secure store instance
     *
     * @param key [String] value of key value pair to remove
     *
     */

    suspend fun remove(key: String)
}
