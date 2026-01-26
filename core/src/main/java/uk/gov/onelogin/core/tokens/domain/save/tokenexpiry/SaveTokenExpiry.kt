package uk.gov.onelogin.core.tokens.domain.save.tokenexpiry

interface SaveTokenExpiry {
    /**
     * Use case to save the expiry time of any token in open shared preferences
     *
     * @param expiry [ExpiryInfo] that will be saved where [ExpiryInfo.key] is the alias for the saved value and the [ExpiryInfo.value] is the actual expiry time saved in the open secure store.
     */
    suspend fun saveExp(vararg expiry: ExpiryInfo)

    fun extractExpFromRefreshToken(tokenJwt: String): Long
}
