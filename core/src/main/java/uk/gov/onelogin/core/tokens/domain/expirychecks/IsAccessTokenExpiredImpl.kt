package uk.gov.onelogin.core.tokens.domain.expirychecks

import java.time.Instant
import javax.inject.Inject
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry
import uk.gov.onelogin.core.utils.AccessToken

class IsAccessTokenExpiredImpl @Inject constructor(
    @AccessToken
    private val getTokenExpiry: GetTokenExpiry
) : IsTokenExpired {
    override fun invoke(): Boolean {
        val tokenExpiry = getTokenExpiry()
        return if (tokenExpiry != null) {
            tokenExpiry < Instant.now().toEpochMilli()
        } else {
            true
        }
    }
}
