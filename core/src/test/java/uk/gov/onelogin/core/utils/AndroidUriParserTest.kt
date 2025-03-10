package uk.gov.onelogin.core.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidUriParserTest {
    private val parser = AndroidUriParser()

    @Test
    fun checkParser() {
        val result = parser.parse("www.google.com")

        assertEquals("www.google.com", result.path)
    }
}
