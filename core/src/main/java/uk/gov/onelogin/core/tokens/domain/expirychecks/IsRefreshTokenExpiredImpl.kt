package uk.gov.onelogin.core.tokens.domain.expirychecks

import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry
import uk.gov.onelogin.core.utils.RefreshToken
import java.time.Instant
import javax.inject.Inject

class IsRefreshTokenExpiredImpl
    @Inject
    constructor(
        @RefreshToken
        private val getTokenExpiry: GetTokenExpiry,
    ) : IsTokenExpired {
        override suspend fun invoke(): Boolean {
            val tokenExpiry = getTokenExpiry()
            return if (tokenExpiry != null) {
                tokenExpiry < Instant.now().epochSecond
            } else {
                true
            }
        }
    }
