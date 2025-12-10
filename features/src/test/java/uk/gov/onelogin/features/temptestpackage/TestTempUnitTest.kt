package uk.gov.onelogin.features.temptestpackage

import kotlin.test.assertEquals
import org.junit.Test

class TestTempUnitTest {
    val sut = TestTemp()

    @Test
    fun test() {
        assertEquals(2, sut.calculate(1, 1))
    }
}
