package uk.gov.onelogin.features.unit.appinfo.data

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage
import uk.gov.logging.api.v3.matchers.MemorisedLoggerMatchers.hasSize
import uk.gov.onelogin.features.TestUtils
import uk.gov.onelogin.features.appinfo.AppInfoUtils
import uk.gov.onelogin.features.appinfo.AppInfoUtilsImpl
import uk.gov.onelogin.features.appinfo.data.AppVersionCheckImpl
import uk.gov.onelogin.features.appinfo.data.model.AppInfoServiceState

class AppVersionCheckImplTest {
    private val appInfoUtils: AppInfoUtils = AppInfoUtilsImpl()
    private val logger = MemorisedLogger()
    private val sut = AppVersionCheckImpl(appInfoUtils, "1.0.0", logger)

    @Test
    fun versionCheckSuccessful() {
        var result = sut.compareVersions(TestUtils.appInfoData)
        assertEquals(AppInfoServiceState.Successful(TestUtils.appInfoData), result)
        result = sut.compareVersions(TestUtils.additionalAppInfoData)
        assertEquals(AppInfoServiceState.Successful(TestUtils.additionalAppInfoData), result)
        assertThat(logger, hasSize(0))
    }

    @Test
    fun versionCheckUpdateRequired() {
        val result = sut.compareVersions(TestUtils.updateRequiredAppInfoData)
        assertEquals(AppInfoServiceState.UpdateRequired, result)
        assertThat(logger, hasSize(0))
    }

    @Test
    fun versionCheckError() {
        val result = sut.compareVersions(TestUtils.extractVersionErrorAppInfoData)
        assertEquals(AppInfoServiceState.Unavailable, result)
        assertThat(logger, hasItem(hasMessage("IllegalVersionFormat")))
    }
}
