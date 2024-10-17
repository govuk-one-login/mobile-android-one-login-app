package uk.gov.onelogin.features.domain

import javax.inject.Inject
import uk.gov.android.features.FeatureFlags
import uk.gov.android.features.InMemoryFeatureFlags
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.features.AppCheckFeatureFlag

fun interface FeatureFlagSetter {
    fun setFromAppInfo(appInfo: AppInfoData.AppInfo)
}

class FeatureFlagSetterImpl @Inject constructor(
    private val featureFlags: FeatureFlags
) : FeatureFlagSetter {
    override fun setFromAppInfo(appInfo: AppInfoData.AppInfo) {
        if (appInfo.featureFlags.appCheckEnabled) {
            (featureFlags as InMemoryFeatureFlags).plusAssign(setOf(AppCheckFeatureFlag.ENABLED))
        } else {
            (featureFlags as InMemoryFeatureFlags).minusAssign(setOf(AppCheckFeatureFlag.ENABLED))
        }
    }
}
