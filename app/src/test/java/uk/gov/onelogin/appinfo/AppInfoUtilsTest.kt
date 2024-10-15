package uk.gov.onelogin.appinfo

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class AppInfoUtilsTest {
    private val correctV = "1.0.0"
    private val correctVersion = "v1.0.0"
    private val longVersion = "1.0.8.0"
    private val incorrectVersion = "vers1.0.0"
    private val sut = AppInfoUtilsImpl()

    @Test
    fun `check correct version format`() {
        val result = sut.getComparableAppVersion(correctV)
        assertEquals(listOf(1, 0, 0), result)
    }

    @Test
    fun `check conventional correct version format`() {
        val result = sut.getComparableAppVersion(correctVersion)
        assertEquals(listOf(1, 0, 0), result)
    }

    @Test()
    fun `conventional commits violation - version has too many arguments`() {
        assertThrows<AppInfoUtils.AppError.IncorrectVersionFormat> {
            sut.getComparableAppVersion(longVersion)
        }
    }

    @Test
    fun `conventional commits violation - illegal characters`() {
        assertThrows<AppInfoUtils.AppError.IllegalVersionFormat> {
            sut.getComparableAppVersion(incorrectVersion)
        }
    }
}
