package uk.gov.onelogin.appinfo.service.domain.model

import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData

sealed class AppInfoServiceState {
    data class Successful(val value: AppInfoData) : AppInfoServiceState()
    data object Unavailable : AppInfoServiceState()
    data object Offline : AppInfoServiceState()
    data object UpdateRequired : AppInfoServiceState()
}
