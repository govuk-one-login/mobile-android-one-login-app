package uk.gov.onelogin.features.test.login.domain.refresh

sealed class RefreshExchangeResult {
    data object Success : RefreshExchangeResult()

    data object ReauthRequired : RefreshExchangeResult()

    data object FirstTimeUser : RefreshExchangeResult()

    data object UnrecoverableError : RefreshExchangeResult()

    data object ClientAttestationFailure : RefreshExchangeResult()

    data object OfflineNetwork : RefreshExchangeResult()

    data object UserCancelledBioPrompt : RefreshExchangeResult()
}
