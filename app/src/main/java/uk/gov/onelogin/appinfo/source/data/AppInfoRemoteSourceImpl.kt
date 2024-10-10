package uk.gov.onelogin.appinfo.source.data

import javax.inject.Inject
import uk.gov.android.network.api.ApiResponse
import uk.gov.onelogin.appinfo.apicall.domain.AppInfoApi
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoRemoteState
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoRemoteSource

class AppInfoRemoteSourceImpl @Inject constructor(
    private val appInfoApi: AppInfoApi
) : AppInfoRemoteSource {
    override suspend fun get(): AppInfoRemoteState {
        return when (val result = appInfoApi.callApi()) {
            is ApiResponse.Success<*> -> AppInfoRemoteState.Success(result.response as AppInfoData)
            is ApiResponse.Offline -> AppInfoRemoteState.Offline
            else -> AppInfoRemoteState.Failure(APP_INFO_REMOTE_SOURCE_ERROR)
        }
    }

    companion object {
        const val APP_INFO_REMOTE_SOURCE_ERROR = "Retrieving AppInfo from remote"
    }
}
