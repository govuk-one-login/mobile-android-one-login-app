package uk.gov.onelogin.core.tokens.domain.expirychecks

import javax.inject.Inject
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry
import uk.gov.onelogin.core.utils.RefreshToken

class IsRefreshTokenExpiredImpl @Inject constructor(
    @RefreshToken
    private val getTokenExpiry: GetTokenExpiry
) : IsAccessTokenExpired {
    override fun invoke(): Boolean {
        val tokenExpiry = getTokenExpiry()
        return if (tokenExpiry != null) {
            tokenExpiry < System.currentTimeMillis()
        } else {
            true
        }
    }
}
