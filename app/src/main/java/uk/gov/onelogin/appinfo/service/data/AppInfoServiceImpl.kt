package uk.gov.onelogin.appinfo.service.data

import javax.inject.Inject
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
        return when (val remoteResult = remoteSource.get()) {
            is AppInfoRemoteState.Success -> {
                localSource.update(remoteResult.value)
                useLocalSource(AppInfoServiceState.Unavailable)
            }
            AppInfoRemoteState.Offline -> useLocalSource(AppInfoServiceState.Offline)
            else -> useLocalSource(AppInfoServiceState.Unavailable)
        }
    }

    private fun useLocalSource(
        fallback: AppInfoServiceState
    ): AppInfoServiceState {
        val localState = localSource.get()
        return if (localState is AppInfoLocalState.Success) {
            // Check app availability
            if (localState.value.apps.android.available) {
                // Check min version compatible
                appVersionCheck.compareVersions(localState.value)
            } else {
                fallback
            }
        } else {
            fallback
        }
    }
}
