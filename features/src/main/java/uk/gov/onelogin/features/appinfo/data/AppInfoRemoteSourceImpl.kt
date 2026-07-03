package uk.gov.onelogin.features.appinfo.data

import uk.gov.android.network.api.ApiResponse
import uk.gov.onelogin.features.appinfo.data.model.AppInfoData
import uk.gov.onelogin.features.appinfo.data.model.AppInfoRemoteState
import uk.gov.onelogin.features.appinfo.domain.AppInfoApi
import uk.gov.onelogin.features.appinfo.domain.AppInfoRemoteSource
import javax.inject.Inject

class AppInfoRemoteSourceImpl
    @Inject
    constructor(
        private val appInfoApi: AppInfoApi,
    ) : AppInfoRemoteSource {
        override suspend fun get(): AppInfoRemoteState =
            when (val result = appInfoApi.callApi()) {
                is ApiResponse.Success<*> ->
                    AppInfoRemoteState.Success(
                        result.response as AppInfoData,
                    )
                is ApiResponse.Offline -> AppInfoRemoteState.Offline
                is ApiResponse.Failure -> AppInfoRemoteState.Failure("Status: ${result.status}", result.error)
                // The Loading response type is never actually returned (due to be fixed in DCMAW-20185)
                ApiResponse.Loading ->
                    AppInfoRemoteState.Failure(
                        APP_INFO_REMOTE_SOURCE_ERROR,
                        error = Exception("Unexpected Loading API response"),
                    )
            }

        companion object {
            const val APP_INFO_REMOTE_SOURCE_ERROR = "Error retrieving AppInfo from remote"
        }
    }
