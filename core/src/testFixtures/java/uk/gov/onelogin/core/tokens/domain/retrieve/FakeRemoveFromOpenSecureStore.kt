package uk.gov.onelogin.core.tokens.domain.retrieve

import uk.gov.onelogin.core.tokens.domain.remove.RemoveFromOpenSecureStore

class FakeRemoveFromOpenSecureStore : RemoveFromOpenSecureStore {
    var exception: Throwable? = null
    val store = mutableMapOf<String, String?>()

    override suspend fun remove(key: String) {
        exception?.let { throw it }
        store.remove(key)
    }
}
