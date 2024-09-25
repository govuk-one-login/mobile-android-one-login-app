package uk.gov.onelogin.tokens.usecases

import javax.inject.Inject

interface IsAccessTokenExpired {
    operator fun invoke(): Boolean
}

class IsAccessTokenExpiredImpl @Inject constructor(
    private val getTokenExpiry: GetTokenExpiry
) : IsAccessTokenExpired {
    override fun invoke() =
        getTokenExpiry()?.let { it < System.currentTimeMillis() } ?: true
}
