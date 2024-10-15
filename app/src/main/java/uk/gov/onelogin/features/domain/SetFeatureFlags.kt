package uk.gov.onelogin.features.domain

import android.util.Log
import javax.inject.Inject
import uk.gov.android.features.FeatureFlags
import uk.gov.android.features.InMemoryFeatureFlags
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoLocalState
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.features.AppCheckFeatureFlag

fun interface SetFeatureFlags {
    fun fromAppInfo()
}

class SetFeatureFlagsImpl @Inject constructor(
    private val featureFlags: FeatureFlags,
    private val appInfoLocalSource: AppInfoLocalSource
) : SetFeatureFlags {
    override fun fromAppInfo() {
        when (val appInfoState = appInfoLocalSource.get()) {
            is AppInfoLocalState.Failure ->
                Log.e(
                    this::class.simpleName,
                    "Failed to read local appInfo: ${appInfoState.reason}",
                    appInfoState.exp
                )

            is AppInfoLocalState.Success ->
                setFeatures(appInfoState.value.apps.android)
        }
    }

    private fun setFeatures(appInfo: AppInfoData.AppInfo) {
        if (appInfo.featureFlags.appCheckEnabled) {
            (featureFlags as InMemoryFeatureFlags).plusAssign(setOf(AppCheckFeatureFlag.ENABLED))
        } else {
            (featureFlags as InMemoryFeatureFlags).minusAssign(setOf(AppCheckFeatureFlag.ENABLED))
        }
    }
}
