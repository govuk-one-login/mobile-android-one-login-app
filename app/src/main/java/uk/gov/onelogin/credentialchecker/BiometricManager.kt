package uk.gov.onelogin.credentialchecker

interface BiometricManager {
    fun canAuthenticate(): BiometricStatus
}
