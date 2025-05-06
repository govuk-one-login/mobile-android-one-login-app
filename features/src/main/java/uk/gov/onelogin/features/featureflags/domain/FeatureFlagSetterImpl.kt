package uk.gov.onelogin.features.featureflags.domain

import javax.inject.Inject
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.featureflags.InMemoryFeatureFlags
import uk.gov.android.onelogin.features.BuildConfig
import uk.gov.onelogin.features.appinfo.data.model.AppInfoData
import uk.gov.onelogin.features.featureflags.data.AppIntegrityFeatureFlag
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

class FeatureFlagSetterImpl @Inject constructor(
    private val featureFlags: FeatureFlags
) : FeatureFlagSetter {
    override fun setFromAppInfo(appInfo: AppInfoData.AppInfo) {
        setAppCheckEnabledFlag(appInfo)
        setWalletEnabledFlag(appInfo)
    }

    private fun setAppCheckEnabledFlag(appInfo: AppInfoData.AppInfo) {
        if (appInfo.featureFlags.appCheckEnabled) {
            (featureFlags as InMemoryFeatureFlags).plusAssign(
                setOf(AppIntegrityFeatureFlag.ENABLED)
            )
        } else {
            (featureFlags as InMemoryFeatureFlags).minusAssign(
                setOf(AppIntegrityFeatureFlag.ENABLED)
            )
        }
    }

    private fun setWalletEnabledFlag(appInfo: AppInfoData.AppInfo) {
        if (appInfo.releaseFlags.walletVisibleToAll) {
            (featureFlags as InMemoryFeatureFlags).plusAssign(setOf(WalletFeatureFlag.ENABLED))
        } else {
            // Temporary fix to enable WalletFeatureFlag by default for build and staging envs
            // Can be removed once walletVisibleToAll is set to true in appInfo API
            if (BuildConfig.FLAVOR != "build" && BuildConfig.FLAVOR != "staging") {
                (featureFlags as InMemoryFeatureFlags).minusAssign(setOf(WalletFeatureFlag.ENABLED))
            }
        }
    }
}
