package uk.gov.onelogin.credentialchecker

interface CredentialChecker {
    fun isDeviceSecure(): Boolean
}
