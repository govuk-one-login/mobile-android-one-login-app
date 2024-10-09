package uk.gov.onelogin.appinfo.source.domain.model

import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource

sealed class AppInfoLocalState {
    data class Success(val value: AppInfoData) : AppInfoLocalState()
    data class Failure(val reason: String) : AppInfoLocalState()
}
