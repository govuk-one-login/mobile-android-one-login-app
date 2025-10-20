package uk.gov.onelogin.core.tokens.domain.expirychecks

fun interface IsAccessTokenExpired {
    operator fun invoke(): Boolean
}
