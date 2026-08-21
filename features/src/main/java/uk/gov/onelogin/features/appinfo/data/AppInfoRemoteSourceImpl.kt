package uk.gov.onelogin.features.appinfo.data

import uk.gov.android.network.api.v3.ApiResponse
import uk.gov.android.network.online.OnlineChecker
import uk.gov.onelogin.features.appinfo.data.model.AppInfoRemoteState
import uk.gov.onelogin.features.appinfo.domain.AppInfoApi
import uk.gov.onelogin.features.appinfo.domain.AppInfoRemoteSource
import javax.inject.Inject

class AppInfoRemoteSourceImpl
    @Inject
    constructor(
        private val appInfoApi: AppInfoApi,
        private val onlineChecker: OnlineChecker,
    ) : AppInfoRemoteSource {
        override suspend fun get(): AppInfoRemoteState {
            if (!onlineChecker.isOnline()) {
                return AppInfoRemoteState.Offline
            }
            return when (val result = appInfoApi.callApi()) {
                is ApiResponse.Success -> AppInfoRemoteState.Success(result.body)
                is ApiResponse.Failure -> AppInfoRemoteState.Failure("Status: ${result.status}", result.error)
            }
        }

        companion object {
            const val APP_INFO_REMOTE_SOURCE_ERROR = "Error retrieving AppInfo from remote"
        }
    }
