package uk.gov.onelogin.login.state

sealed class LocalAuthStatus {
    data class Success(
        val accessToken: String
    ) : LocalAuthStatus()
    data object UserCancelled : LocalAuthStatus()
    data object BioCheckFailed : LocalAuthStatus()
    data object SecureStoreError : LocalAuthStatus()
    data object ManualSignIn : LocalAuthStatus()
}
