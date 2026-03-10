package uk.gov.onelogin.core.tokens.domain.idtoken.iss

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import uk.gov.android.onelogin.core.BuildConfig
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.tokens.utils.JwtExtractor
import uk.gov.onelogin.core.tokens.utils.JwtExtractorImpl
import kotlin.io.encoding.Base64
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ExtractAndVerifyIssuerlImplTest {
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

    val sut = ExtractAndVerifyIssuerImpl(extractFromJson, logger)

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

    private fun createJwtBody(): String {
        val expectedIssValue = "https://token.${BuildConfig.FLAVOR.lowercase()}.account.gov.uk"
        val jsonObject =
            buildJsonObject {
                put("iss", expectedIssValue)
            }
        return Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).encode(
            jsonObject.toString().toByteArray()
        )
    }
}
