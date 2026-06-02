package uk.gov.onelogin.core.tokens.domain.retrieve

class FakeGetFromOpenSecureStore :
    GetFromOpenSecureStore,
    MutableMap<String, String?> by mutableMapOf() {
    override suspend fun invoke(vararg key: String): Map<String, String?>? = filterKeys { it in key }.ifEmpty { null }
}
