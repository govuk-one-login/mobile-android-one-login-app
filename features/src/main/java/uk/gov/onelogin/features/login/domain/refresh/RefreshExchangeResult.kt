package uk.gov.onelogin.features.login.domain.refresh

sealed class RefreshExchangeResult {
    data object Success : RefreshExchangeResult()

    data object ReAuthRequired : RefreshExchangeResult()

    data object SignInRequired : RefreshExchangeResult()

    data object ClientAttestationFailure : RefreshExchangeResult()

    data object OfflineNetwork : RefreshExchangeResult()

    data object UserCancelledBioPrompt : RefreshExchangeResult()

    data object BioCheckFailed : RefreshExchangeResult()
}
