package uk.gov.onelogin.appinfo.appversioncheck

import junit.framework.TestCase.assertEquals
import org.junit.Test
import uk.gov.onelogin.TestUtils
import uk.gov.onelogin.appinfo.AppInfoUtils
import uk.gov.onelogin.appinfo.AppInfoUtilsImpl
import uk.gov.onelogin.appinfo.appversioncheck.data.AppVersionCheckImpl
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState

class AppVersionCheckImplTest {
    private val appInfoUtils: AppInfoUtils = AppInfoUtilsImpl()
    private val sut = AppVersionCheckImpl(appInfoUtils)

    @Test
    fun versionCheckSuccessful() {
        val result = sut.compareVersions(TestUtils.data)
        assertEquals(AppInfoServiceState.Successful(TestUtils.data), result)
    }

    @Test
    fun versionCheckUpdateRequired() {
        val result = sut.compareVersions(TestUtils.updateRequiredData)
        assertEquals(AppInfoServiceState.UpdateRequired, result)
    }

    @Test
    fun versionCheckError() {
        val result = sut.compareVersions(TestUtils.extractVersionErrorData)
        assertEquals(AppInfoServiceState.Unavailable, result)
    }
}
