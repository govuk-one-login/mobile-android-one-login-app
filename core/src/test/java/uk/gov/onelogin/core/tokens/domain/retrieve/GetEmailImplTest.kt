package uk.gov.onelogin.core.tokens.domain.retrieve

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import uk.gov.logging.testdouble.SystemLogger

class GetEmailImplTest {
    private val expectedEmail = "email@mail.com"
    private val idTokenWithEmail =
        "eyJhbGciOiJIUzI1NiJ9" +
            ".eyJlbWFpbCI6ImVtYWlsQG1haWwuY29tIn0" + // payload contains "email": "email@mail.com"
            ".mHuqqrjGNsVpzm-8jiZ8VnlWuAVSlexyjDsOX7YDB6Q"
    private val idTokenWithoutEmail =
        "eyJhbGciOiJIUzI1NiJ9" +
            ".e30." + // no email in the payload
            "ZRrHA1JJJW8opsbCGfG_HACGpVUMN_a9IV7pAx_Zmeo"
    private val logger = SystemLogger()

    val sut = GetEmailImpl(logger)

    @Test
    fun successScenario() {
        val emailResponse = sut.invoke(idTokenWithEmail)
        assertEquals(expectedEmail, emailResponse)
    }

    @Test
    fun missingEmailScenario() {
        val emailResponse = sut.invoke(idTokenWithoutEmail)
        assertEquals(null, emailResponse)
    }

    @Test
    fun malformedIdTokenScenario() {
        val emailResponse = sut.invoke("not an id token")
        assertEquals(null, emailResponse)
        assertEquals(1, logger.size)
    }
}
