package uk.gov.onelogin.credentialchecker

fun interface BiometricManager {
    fun canAuthenticate(): BiometricStatus
}
