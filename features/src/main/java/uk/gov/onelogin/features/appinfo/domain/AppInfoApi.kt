package uk.gov.onelogin.features.appinfo.domain

import uk.gov.onelogin.features.appinfo.data.model.AppInfoData

sealed interface AppInfoApiResult {
    data class Success(val data: AppInfoData) : AppInfoApiResult
    data class Failure(val status: Int, val error: Exception) : AppInfoApiResult
}

fun interface AppInfoApi {
    suspend fun callApi(): AppInfoApiResult
}
