package uk.gov.onelogin.appinfo.appversioncheck

import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test
import uk.gov.onelogin.TestUtils
import uk.gov.onelogin.appinfo.AppInfoUtils
import uk.gov.onelogin.appinfo.AppInfoUtilsImpl
import uk.gov.onelogin.appinfo.appversioncheck.data.AppVersionCheckImpl
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState

class AppVersionCheckImplTest {
    private val appInfoUtils: AppInfoUtils = AppInfoUtilsImpl()
    private val sut = AppVersionCheckImpl(appInfoUtils, "1.0.0")

    @Test
    fun versionCheckSuccessful() {
        var result = sut.compareVersions(TestUtils.appInfoData)
        assertEquals(AppInfoServiceState.Successful(TestUtils.appInfoData), result)
        result = sut.compareVersions(TestUtils.additionalAppInfoData)
        assertEquals(AppInfoServiceState.Successful(TestUtils.additionalAppInfoData), result)
    }

    @Test
    fun versionCheckUpdateRequired() {
        val result = sut.compareVersions(TestUtils.updateRequiredAppInfoData)
        assertEquals(AppInfoServiceState.UpdateRequired, result)
    }

    @Test
    fun versionCheckError() {
        val result = sut.compareVersions(TestUtils.extractVersionErrorAppInfoData)
        assertEquals(AppInfoServiceState.Unavailable, result)
    }
}
