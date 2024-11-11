package uk.gov.onelogin.appcheck

fun interface AppIntegrity {
    suspend fun startCheck(): AppIntegrityResult

    companion object {
        const val CLIENT_ATTESTATION = "appCheckClientAttestation"
        const val CLIENT_ATTESTATION_EXPIRY = "appCheckClientAttestation"
        const val SECURE_STORE_ERROR = "ERROR: Saving to open secure store error"
    }
}
