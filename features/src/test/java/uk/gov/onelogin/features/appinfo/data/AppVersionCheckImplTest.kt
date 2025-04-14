package uk.gov.onelogin.features.appinfo.data

import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.features.TestUtils
import uk.gov.onelogin.features.appinfo.AppInfoUtils
import uk.gov.onelogin.features.appinfo.AppInfoUtilsImpl
import uk.gov.onelogin.features.appinfo.data.model.AppInfoServiceState

class AppVersionCheckImplTest {
    private val appInfoUtils: AppInfoUtils = AppInfoUtilsImpl()
    private val logger = SystemLogger()
    private val sut = AppVersionCheckImpl(appInfoUtils, "1.0.0", logger)

    @Test
    fun versionCheckSuccessful() {
        var result = sut.compareVersions(TestUtils.appInfoData)
        assertEquals(AppInfoServiceState.Successful(TestUtils.appInfoData), result)
        result = sut.compareVersions(TestUtils.additionalAppInfoData)
        assertEquals(AppInfoServiceState.Successful(TestUtils.additionalAppInfoData), result)
        assertTrue(logger.size == 0)
    }

    @Test
    fun versionCheckUpdateRequired() {
        val result = sut.compareVersions(TestUtils.updateRequiredAppInfoData)
        assertEquals(AppInfoServiceState.UpdateRequired, result)
        assertTrue(logger.size == 0)
    }

    @Test
    fun versionCheckError() {
        val result = sut.compareVersions(TestUtils.extractVersionErrorAppInfoData)
        assertEquals(AppInfoServiceState.Unavailable, result)
        assertTrue(logger.contains("IllegalVersionFormat"))
    }
}
