package uk.gov.onelogin.features.temptestpackage

import org.junit.Test
import kotlin.test.assertEquals

class TestTempUnitTest {
    val sut = TestTemp()
    @Test
    fun test() {
        assertEquals(2, sut.calculate(1, 1))
    }
}
