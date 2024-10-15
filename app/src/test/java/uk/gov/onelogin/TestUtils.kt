package uk.gov.onelogin

import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData

object TestUtils {
    val data = AppInfoData(
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

    val updateRequiredData = AppInfoData(
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

    val extractVersionErrorData = AppInfoData(
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
