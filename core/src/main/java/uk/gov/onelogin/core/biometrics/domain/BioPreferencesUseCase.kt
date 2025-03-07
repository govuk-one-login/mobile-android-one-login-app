package uk.gov.onelogin.core.biometrics.domain

fun interface BioPreferencesUseCase {
    suspend fun reset()
}
