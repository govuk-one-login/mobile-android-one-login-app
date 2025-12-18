package uk.gov.onelogin.core.tokens.domain.retrieve

import javax.inject.Inject
import kotlin.text.toLong
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY

class GetAccessTokenExpiryImpl @Inject constructor(
    private val getFromOpenSecureStore: GetFromOpenSecureStore
) : GetTokenExpiry {
    override suspend fun invoke(): Long? {
        val expiryTimestamp = getFromOpenSecureStore(ACCESS_TOKEN_EXPIRY_KEY)
            ?.get(ACCESS_TOKEN_EXPIRY_KEY)?.toLong()
        return if (expiryTimestamp == 0L) {
            null
        } else {
            expiryTimestamp
        }
    }
}
