package uk.gov.onelogin.appinfo.source.domain.model

import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData

sealed class AppInfoRemoteState {
    data class Success(val value: AppInfoData) : AppInfoRemoteState()
    data class Failure(val reason: String) : AppInfoRemoteState()
    data object Offline : AppInfoRemoteState()
}
