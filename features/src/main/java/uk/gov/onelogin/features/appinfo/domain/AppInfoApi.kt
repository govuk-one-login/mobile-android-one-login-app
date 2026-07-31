package uk.gov.onelogin.features.appinfo.domain

import uk.gov.android.network.api.v3.ApiResponse
import uk.gov.android.network.service.NetworkingException
import uk.gov.onelogin.features.appinfo.data.model.AppInfoData

fun interface AppInfoApi {
    suspend fun callApi(): ApiResponse<AppInfoData, String, NetworkingException>
}
