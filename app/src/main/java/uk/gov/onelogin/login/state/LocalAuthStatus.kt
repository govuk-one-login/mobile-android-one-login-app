package uk.gov.onelogin.login.state

sealed class LocalAuthStatus {
    data class Success(
        val accessToken: String
    ) : LocalAuthStatus()
    object UserCancelled : LocalAuthStatus()
    object BioCheckFailed : LocalAuthStatus()
    object SecureStoreError : LocalAuthStatus()
    object RefreshToken : LocalAuthStatus()
}
