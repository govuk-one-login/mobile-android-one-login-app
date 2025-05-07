package uk.gov.onelogin.core.tokens.domain.save

fun interface SavePersistentId {
    suspend operator fun invoke()
}
