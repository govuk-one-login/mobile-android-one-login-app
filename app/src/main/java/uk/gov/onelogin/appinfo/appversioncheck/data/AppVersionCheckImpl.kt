package uk.gov.onelogin.appinfo.appversioncheck.data

import android.util.Log
import javax.inject.Inject
import uk.gov.onelogin.appinfo.AppInfoUtils
import uk.gov.onelogin.appinfo.BuildConfigVersion
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.appversioncheck.domain.AppVersionCheck
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState

class AppVersionCheckImpl @Inject constructor(
    private val utils: AppInfoUtils,
    @BuildConfigVersion
    private val appVersion: String
) : AppVersionCheck {
    override fun compareVersions(data: AppInfoData): AppInfoServiceState {
        val updatedVersion = data.apps.android.minimumVersion
        lateinit var result: AppInfoServiceState
        try {
            val localVersion = utils.getComparableAppVersion(appVersion)
            val serverVersion = utils.getComparableAppVersion(updatedVersion)
            var discrepancyFound = false
            for (i in serverVersion.indices) {
                // Exit the loop if any of the version parts is bigger - avoid returning [UpdateRequired] when
                // local version is 1.0.0 but server version is 0.1.0
                if (localVersion[i] > serverVersion[i]) {
                    result = AppInfoServiceState.Successful(data)
                    discrepancyFound = true
                } else if (localVersion[i] < serverVersion[i]) {
                    result = AppInfoServiceState.UpdateRequired
                    discrepancyFound = true
                } else {
                    result = AppInfoServiceState.Successful(data)
                }
                if (discrepancyFound) {
                    break
                }
            }
        } catch (e: AppInfoUtils.AppError) {
            Log.e(TAG, e.toString())
            result = AppInfoServiceState.Unavailable
        }
        return result
    }

    companion object {
        private const val TAG = "AppVersionCheck"
    }
}
