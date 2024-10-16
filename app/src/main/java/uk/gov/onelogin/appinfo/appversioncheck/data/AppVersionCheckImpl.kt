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
            Log.d("AppVersionLocal", "$localVersion")
            val serverVersion = utils.getComparableAppVersion(updatedVersion)
            for (i in serverVersion.indices) {
                if (localVersion[i] < serverVersion[i]) {
                    result = AppInfoServiceState.UpdateRequired
                    break
                } else {
                    result = AppInfoServiceState.Successful(data)
                }
            }
        } catch (e: AppInfoUtils.AppError) {
            Log.e(TAG, e.toString())
            result = AppInfoServiceState.Unavailable
        }
        return result
    }

    companion object {
        const val TAG = "AppVersionCheck"
    }
}
