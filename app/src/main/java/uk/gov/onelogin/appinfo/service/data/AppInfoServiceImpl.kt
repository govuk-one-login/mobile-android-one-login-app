package uk.gov.onelogin.appinfo.service.data

import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.service.domain.AppInfoService
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoLocalState
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoRemoteState
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoRemoteSource
import javax.inject.Inject

class AppInfoServiceImpl @Inject constructor(
    private val remoteSource: AppInfoRemoteSource,
    private val localSource: AppInfoLocalSource
) : AppInfoService {
    override suspend fun get(): AppInfoServiceState {
        return when (val remoteResult = remoteSource.get()) {
            is AppInfoRemoteState.Success -> {
                val encodedValue = Json.encodeToString<AppInfoData>(remoteResult.value)
                localSource.update(encodedValue)
                return AppInfoServiceState.RemoteSuccess(remoteResult.value)
            }
            else -> useLocalService()
        }
    }

    private suspend fun useLocalService() = when (val localResult = localSource.get()) {
        is AppInfoLocalState.Success -> {
            AppInfoServiceState.LocalSuccess(localResult.value)
        }

        else -> AppInfoServiceState.Nothing
    }
}