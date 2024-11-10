package uk.gov.onelogin.appcheck

fun interface AppIntegrity {
    suspend fun startCheck(): AppIntegrityResult
}
