package uk.gov.onelogin.appinfo.source.domain.model

import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData

sealed class AppInfoLocalState {
    data class Success(val value: AppInfoData) : AppInfoLocalState()
    data class Failure(val reason: String, val exp: Exception? = null) : AppInfoLocalState()
}
