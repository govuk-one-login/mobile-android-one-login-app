package uk.gov.onelogin.core.tokens.domain.expirychecks

fun interface IsTokenExpired {
    operator fun invoke(): Boolean
}
