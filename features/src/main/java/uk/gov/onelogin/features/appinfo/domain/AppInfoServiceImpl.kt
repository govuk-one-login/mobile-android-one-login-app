package uk.gov.onelogin.features.appinfo.domain

import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.utils.OneLoginInjectionAnnotation
import uk.gov.onelogin.features.appinfo.data.model.AppInfoLocalState
import uk.gov.onelogin.features.appinfo.data.model.AppInfoRemoteState
import uk.gov.onelogin.features.appinfo.data.model.AppInfoServiceState
import javax.inject.Inject

class AppInfoServiceImpl
    @Inject
    constructor(
        private val remoteSource: AppInfoRemoteSource,
        private val localSource: AppInfoLocalSource,
        private val appVersionCheck: AppVersionCheck,
        @OneLoginInjectionAnnotation
        private val logger: Logger
    ) : AppInfoService {
        override suspend fun get(): AppInfoServiceState =
            when (val remoteResult = remoteSource.get()) {
                is AppInfoRemoteState.Success -> {
                    localSource.update(remoteResult.value)
                    useLocalSource(AppInfoServiceState.Unavailable)
                }
                AppInfoRemoteState.Offline -> useLocalSource(AppInfoServiceState.Offline)
                is AppInfoRemoteState.Failure -> {
                    logger.error(TAG, remoteResult.reason)
                    useLocalSource(AppInfoServiceState.Unavailable)
                }
            }

        private fun useLocalSource(fallback: AppInfoServiceState): AppInfoServiceState {
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

        companion object {
            private const val TAG = "AppInfoServiceError"
        }
    }
