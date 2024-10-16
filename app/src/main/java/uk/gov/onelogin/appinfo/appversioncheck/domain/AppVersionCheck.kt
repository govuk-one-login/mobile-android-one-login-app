package uk.gov.onelogin.appinfo.appversioncheck.domain

import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState

fun interface AppVersionCheck {
    fun compareVersions(data: AppInfoData): AppInfoServiceState
}
