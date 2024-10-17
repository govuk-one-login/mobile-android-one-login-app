package uk.gov.onelogin

import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData

object TestUtils {
    val appInfoData = AppInfoData(
        apps = AppInfoData.App(
            AppInfoData.AppInfo(
                minimumVersion = "1.0.0",
                releaseFlags = AppInfoData.ReleaseFlags(
                    true,
                    true,
                    true
                ),
                available = true,
                featureFlags = AppInfoData.FeatureFlags(true)
            )
        )
    )

    val updateRequiredAppInfoData = AppInfoData(
        apps = AppInfoData.App(
            AppInfoData.AppInfo(
                minimumVersion = "2.0.0",
                releaseFlags = AppInfoData.ReleaseFlags(
                    true,
                    true,
                    true
                ),
                available = true,
                featureFlags = AppInfoData.FeatureFlags(true)
            )
        )
    )

    val extractVersionErrorAppInfoData = AppInfoData(
        apps = AppInfoData.App(
            AppInfoData.AppInfo(
                minimumVersion = "vers1.0.0",
                releaseFlags = AppInfoData.ReleaseFlags(
                    true,
                    true,
                    true
                ),
                available = true,
                featureFlags = AppInfoData.FeatureFlags(true)
            )
        )
    )
}
