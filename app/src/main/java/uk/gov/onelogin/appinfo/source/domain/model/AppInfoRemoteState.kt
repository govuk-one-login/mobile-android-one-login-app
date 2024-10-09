package uk.gov.onelogin.appinfo.source.domain.model

import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoRemoteSource

sealed class AppInfoRemoteState {
    data class Success(val value: AppInfoData) : AppInfoRemoteState()
    data class Failure(val reason: String) : AppInfoRemoteState()
    data object Offline : AppInfoRemoteState()
}