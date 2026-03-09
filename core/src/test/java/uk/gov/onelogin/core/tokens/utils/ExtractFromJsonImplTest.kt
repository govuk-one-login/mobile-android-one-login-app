package uk.gov.onelogin.core.tokens.utils

import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ExtractFromJsonImplTest {
    private val expectedValue = "value"
    private val extractionKey = "test"
    private val validJwt =
        "eyJhbGciOiJIUzI1NiJ9" +
            ".ewoidGVzdCI6ICJ2YWx1ZSIKfQ" +
            ".mHuqqrjGNsVpzm-8jiZ8VnlWuAVSlexyjDsOX7YDB6Q"
    private val invalidJwt =
        "eyJhbGciOiJIUzI1NiJ9" +
            ".ewoidGVzdF8xIjogInZhbHVlIgp9" +
            ".ZRrHA1JJJW8opsbCGfG_HACGpVUMN_a9IV7pAx_Zmeo"

    val sut = JwtExtractorImpl()

    @Test
    fun `success extraction`() {
        val result = sut.extractString(validJwt, extractionKey)
        assertEquals(expectedValue, result)
    }

    @Test
    fun `value does not exist`() {
        val result = sut.extractString(invalidJwt, extractionKey)
        assertEquals(null, result)
    }

    @Test
    fun `throws SerializationException`() {
        assertFailsWith<SerializationException> {
            sut.extractString("invalid.InRlc3RfMSI6ICJ2YWx1ZSI.error", extractionKey)
        }
    }
}
