package uk.gov.onelogin.core.tokens.domain.retrieve

import javax.inject.Inject
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY

class GetRefreshTokenExpiryImpl @Inject constructor(
    private val getFromOpenSecureStore: GetFromOpenSecureStore
) : GetTokenExpiry {

    override suspend fun invoke(): Long? {
        val expiryTimestamp = getFromOpenSecureStore(REFRESH_TOKEN_EXPIRY_KEY)
            ?.get(REFRESH_TOKEN_EXPIRY_KEY)?.toLong()
        return if (expiryTimestamp == 0L) {
            null
        } else {
            expiryTimestamp
        }
    }
}
