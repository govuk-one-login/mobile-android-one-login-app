package uk.gov.onelogin.core.tokens.domain.expirychecks

fun interface IsTokenExpired {
    suspend operator fun invoke(): Boolean
}
