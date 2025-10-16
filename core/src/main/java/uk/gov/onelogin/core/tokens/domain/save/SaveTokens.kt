package uk.gov.onelogin.core.tokens.domain.save

fun interface SaveTokens {
    suspend fun save(refreshToken: String?)
}
