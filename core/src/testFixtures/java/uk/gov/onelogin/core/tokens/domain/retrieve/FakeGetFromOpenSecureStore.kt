package uk.gov.onelogin.core.tokens.domain.retrieve

class FakeGetFromOpenSecureStore :
    GetFromOpenSecureStore,
    MutableMap<String, String?> by mutableMapOf() {
    var exception: Throwable? = null
        private set

    fun throwException(e: Throwable) {
        exception = e
    }

    override suspend fun invoke(vararg key: String): Map<String, String?>? {
        exception?.let { throw it }
        return filterKeys { it in key }.ifEmpty { null }
    }
}
