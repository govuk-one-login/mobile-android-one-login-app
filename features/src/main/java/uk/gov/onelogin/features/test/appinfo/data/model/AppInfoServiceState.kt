package uk.gov.onelogin.features.test.appinfo.data.model

sealed class AppInfoServiceState {
    data class Successful(
        val value: AppInfoData,
    ) : AppInfoServiceState()

    data object Unavailable : AppInfoServiceState()

    data object Offline : AppInfoServiceState()

    data object UpdateRequired : AppInfoServiceState()
}
