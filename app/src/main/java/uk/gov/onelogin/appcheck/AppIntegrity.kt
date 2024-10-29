package uk.gov.onelogin.appcheck

interface AppIntegrity {
    suspend fun startCheck(): AppIntegrityResult
}
