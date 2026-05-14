package uk.gov.onelogin.features.appinfo.domain

import uk.gov.logging.api.LogTagProvider
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
import uk.gov.onelogin.core.tokens.data.ApiInfoException
import uk.gov.onelogin.features.appinfo.data.model.AppInfoLocalState
import uk.gov.onelogin.features.appinfo.data.model.AppInfoRemoteState
import uk.gov.onelogin.features.appinfo.data.model.AppInfoServiceState
import uk.gov.onelogin.features.featureflags.domain.FeatureFlagSetter
import javax.inject.Inject

class AppInfoServiceImpl
    @Inject
    constructor(
        private val remoteSource: AppInfoRemoteSource,
        private val localSource: AppInfoLocalSource,
        private val appVersionCheck: AppVersionCheck,
        private val featureFlagSetter: FeatureFlagSetter,
        private val logger: Logger,
    ) : AppInfoService,
        LogTagProvider {
        override suspend fun get(): AppInfoServiceState =
            when (val remoteResult = remoteSource.get()) {
                is AppInfoRemoteState.Success -> {
                    localSource.update(remoteResult.value)
                    useLocalSource(AppInfoServiceState.Unavailable)
                }
                AppInfoRemoteState.Offline -> useLocalSource(AppInfoServiceState.Offline)
                is AppInfoRemoteState.Failure -> {
                    val apiException = ApiInfoException(remoteResult.error)
                    logger.error(
                        remoteResult.reason,
                        apiException,
                        componentKey(COMPONENT),
                        actionKey(ACTION),
                    )
                    useLocalSource(AppInfoServiceState.Unavailable)
                }
            }

        private fun useLocalSource(fallback: AppInfoServiceState): AppInfoServiceState {
            val localState = localSource.get()
            return if (localState is AppInfoLocalState.Success) {
                featureFlagSetter.setFromAppInfo(localState.value.apps.android)
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
            internal const val COMPONENT = "app_info"
            internal const val ACTION = "Get remote app info"
        }
    }
