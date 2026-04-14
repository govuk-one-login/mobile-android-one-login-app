package uk.gov.onelogin.core.tokens.domain.idtoken.iss

import android.content.Context
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.onelogin.core.BuildConfig
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.tokens.utils.JwtExtractor
import uk.gov.onelogin.core.tokens.utils.JwtExtractorImpl
import kotlin.io.encoding.Base64
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ExtractAndVerifyIssuerImplTest {
    private val context: Context = mock()
    private val logger = SystemLogger()
    private val idTokenWithIss =
        "eyJhbGciOiJIUzI1NiJ9" +
            ".${createJwtBody()}" + // payload contains "iss": "https://token.$env.account.gov.uk"
            ".mHuqqrjGNsVpzm-8jiZ8VnlWuAVSlexyjDsOX7YDB6Q"
    private val idTokenWithoutIss =
        "eyJhbGciOiJIUzI1NiJ9" +
            ".e30." + // no email in the payload
            "ZRrHA1JJJW8opsbCGfG_HACGpVUMN_a9IV7pAx_Zmeo"
    private val extractFromJson: JwtExtractor = JwtExtractorImpl()

    val sut = ExtractAndVerifyIssuerImpl(context, extractFromJson, logger)

    @BeforeEach
    fun setup() {
        if (BuildConfig.FLAVOR == "production") {
            whenever(context.getString(any()))
                .thenReturn("https://token.account.gov.uk")
        } else {
            whenever(context.getString(any()))
                .thenReturn("https://token.${BuildConfig.FLAVOR.lowercase()}.account.gov.uk")
        }
    }

    @Test
    fun `success scenario`() {
        assertTrue(sut.verify(idTokenWithIss))
    }

    @Test
    fun `missing email scenario`() {
        assertFalse(sut.verify(idTokenWithoutIss))
    }

    @Test
    fun `malformed Id token scenario`() {
        assertFalse(sut.verify("id token malformed"))
        assertEquals(1, logger.size)
    }

    @Test
    fun `malformed Id token scenario - iss invalid`() {
        val invalidIssToken =
            "eyJhbGciOiJIUzI1NiJ9" +
                ".${createJwtBody(false)}" +
                ".mHuqqrjGNsVpzm-8jiZ8VnlWuAVSlexyjDsOX7YDB6Q"
        assertFalse(sut.verify(invalidIssToken))
    }

    private fun createJwtBody(issValid: Boolean = true): String {
        val expectedIssValue =
            if (issValid) {
                if (BuildConfig.FLAVOR == "production") {
                    "https://token.account.gov.uk"
                } else {
                    "https://token.${BuildConfig.FLAVOR.lowercase()}.account.gov.uk"
                }
            } else {
                "https://wrong.iss/uk"
            }
        val jsonObject =
            buildJsonObject {
                put("iss", expectedIssValue)
            }
        return Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).encode(
            jsonObject.toString().toByteArray()
        )
    }
}
