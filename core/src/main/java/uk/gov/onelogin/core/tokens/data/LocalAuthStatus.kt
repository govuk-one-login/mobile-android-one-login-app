package uk.gov.onelogin.core.tokens.data

sealed class LocalAuthStatus {
    data class Success(
        val payload: Map<String, String?>?,
    ) : LocalAuthStatus()

    data object UserCancelledBioPrompt : LocalAuthStatus()

    data object FirstTimeUser : LocalAuthStatus()

    data object UnrecoverableError : LocalAuthStatus()

    data object ReauthRequired : LocalAuthStatus()
}
