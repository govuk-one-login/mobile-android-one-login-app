package uk.gov.onelogin.core.localauth.domain

fun interface LocalAuthPrefResetUseCase {
    suspend fun reset()
}
