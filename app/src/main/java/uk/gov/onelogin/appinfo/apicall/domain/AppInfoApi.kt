package uk.gov.onelogin.appinfo.apicall.domain

import uk.gov.android.network.api.ApiResponse

fun interface AppInfoApi {
    suspend fun callApi(): ApiResponse
}
