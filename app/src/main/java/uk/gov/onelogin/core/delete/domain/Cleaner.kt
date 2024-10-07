package uk.gov.onelogin.core.delete.domain

fun interface Cleaner {
    suspend fun clean(): Result<Unit>
}
