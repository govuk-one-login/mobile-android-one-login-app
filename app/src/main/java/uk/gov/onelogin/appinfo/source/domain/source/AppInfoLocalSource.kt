package uk.gov.onelogin.appinfo.source.domain.source

import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoLocalState

interface AppInfoLocalSource {
    suspend fun get(): AppInfoLocalState
    suspend fun update(value: String)
}
