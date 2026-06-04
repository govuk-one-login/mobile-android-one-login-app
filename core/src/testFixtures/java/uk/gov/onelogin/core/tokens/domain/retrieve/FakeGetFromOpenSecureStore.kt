package uk.gov.onelogin.core.tokens.domain.retrieve

class FakeGetFromOpenSecureStore :
    GetFromOpenSecureStore,
    MutableMap<String, String?> by mutableMapOf() {
    private var exception: Throwable? = null
    private var returnsNull = false

    fun throwException(e: Throwable) {
        exception = e
    }

    fun returnsNull() {
        returnsNull = true
    }

    override suspend fun invoke(vararg key: String): Map<String, String?>? {
        exception?.let { throw it }
        if (returnsNull) return null
        return filterKeys { it in key }.ifEmpty { null }
    }
}
