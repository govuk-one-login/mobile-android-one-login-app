package uk.gov.onelogin.core.tokens.domain

import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry
import javax.inject.Inject

fun interface IsAccessTokenExpired {
    operator fun invoke(): Boolean
}

class IsAccessTokenExpiredImpl
    @Inject
    constructor(
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
