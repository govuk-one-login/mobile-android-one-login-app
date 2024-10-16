package uk.gov.onelogin.appinfo.service.data

import javax.inject.Inject
import uk.gov.onelogin.appinfo.service.domain.AppInfoService
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoLocalState
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoRemoteState
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoRemoteSource

class AppInfoServiceImpl @Inject constructor(
    private val remoteSource: AppInfoRemoteSource,
    private val localSource: AppInfoLocalSource
) : AppInfoService {
    override suspend fun get(): AppInfoServiceState {
        val localState = localSource.get()
        return when (val remoteResult = remoteSource.get()) {
            is AppInfoRemoteState.Success -> {
                this.localSource.update(remoteResult.value)
                return AppInfoServiceState.RemoteSuccess(remoteResult.value)
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
            AppInfoServiceState.LocalSuccess(localSourceState.value)
        } else {
            fallback
        }
    }
}
