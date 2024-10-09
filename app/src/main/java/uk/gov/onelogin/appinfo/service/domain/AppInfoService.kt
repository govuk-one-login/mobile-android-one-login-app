package uk.gov.onelogin.appinfo.service.domain

import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState

interface AppInfoService {
    suspend fun get(): AppInfoServiceState
}
