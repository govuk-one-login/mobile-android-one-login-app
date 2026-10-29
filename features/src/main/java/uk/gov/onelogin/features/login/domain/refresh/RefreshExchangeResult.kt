package uk.gov.onelogin.features.login.domain.refresh

sealed class RefreshExchangeResult {
    data object Success : RefreshExchangeResult()

    /**
     * The refresh exchange wasn't successful but the caller may treat it as a success
     * for now and try again later.
     */
    data object RetryLater : RefreshExchangeResult()

    data object ReauthRequired : RefreshExchangeResult()

    data object FirstTimeUser : RefreshExchangeResult()

    data object UnrecoverableError : RefreshExchangeResult()

    data object ClientAttestationFailure : RefreshExchangeResult()

    data object OfflineNetwork : RefreshExchangeResult()

    data object UserCancelledBioPrompt : RefreshExchangeResult()
}
