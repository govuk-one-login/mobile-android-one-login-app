package uk.gov.onelogin.core.tokens.domain.remove

fun interface RemoveRefreshTokenAndExpiry {
    suspend fun remove()
}
