package uk.gov.onelogin.appinfo.service.data

import javax.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.appversioncheck.domain.AppVersionCheck
import uk.gov.onelogin.appinfo.service.domain.AppInfoService
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoLocalState
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoRemoteState
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoRemoteSource

class AppInfoServiceImpl @Inject constructor(
    private val remoteSource: AppInfoRemoteSource,
    private val localSource: AppInfoLocalSource,
    private val appVersionCheck: AppVersionCheck
) : AppInfoService {
    override suspend fun get(): AppInfoServiceState {
        val localState = localSource.get()
        return when (val remoteResult = remoteSource.get()) {
            is AppInfoRemoteState.Success -> {
                // Check remote min version compatible
                when (val compareResult = appVersionCheck.compareVersions(remoteResult.value)) {
                    is AppInfoServiceState.Successful -> {
                        // Encode value to save in SharedPrefs and save/ update
                        val encodedValue = Json.encodeToString<AppInfoData>(remoteResult.value)
                        localSource.update(encodedValue)
                        AppInfoServiceState.Successful(remoteResult.value)
                    }
                    else -> compareResult
                }
            }
            AppInfoRemoteState.Offline -> useLocalSource(localState, AppInfoServiceState.Offline)
            is AppInfoRemoteState.Failure ->
                useLocalSource(localState, AppInfoServiceState.Unavailable)
        }
    }

    private fun useLocalSource(
        localSourceState: AppInfoLocalState,
        fallback: AppInfoServiceState
    ): AppInfoServiceState {
        return if (localSourceState is AppInfoLocalState.Success) {
            // Check local min version compatible
            appVersionCheck.compareVersions(localSourceState.value)
            AppInfoServiceState.Successful(localSourceState.value)
        } else {
            fallback
        }
    }
}
