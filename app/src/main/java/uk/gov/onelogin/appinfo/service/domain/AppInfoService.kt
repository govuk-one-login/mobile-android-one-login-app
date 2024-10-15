package uk.gov.onelogin.appinfo.service.domain

import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState

fun interface AppInfoService {
    suspend fun get(): AppInfoServiceState
}
