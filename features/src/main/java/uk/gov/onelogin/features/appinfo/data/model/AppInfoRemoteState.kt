package uk.gov.onelogin.features.appinfo.data.model

sealed class AppInfoRemoteState {
    data class Success(
        val value: AppInfoData,
    ) : AppInfoRemoteState()

    data class Failure(
        val reason: String,
        val error: Throwable? = null,
    ) : AppInfoRemoteState()

    data object Offline : AppInfoRemoteState()
}
