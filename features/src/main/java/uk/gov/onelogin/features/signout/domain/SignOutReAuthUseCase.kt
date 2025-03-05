package uk.gov.onelogin.features.signout.domain

interface SignOutReAuthUseCase {
    suspend fun resetBioPreferences()
}
