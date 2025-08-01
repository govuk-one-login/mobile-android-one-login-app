package uk.gov.onelogin.core.counter

import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class CounterTest {
    private lateinit var sut: Counter

    @Before
    fun setup() {
        sut = CounterImpl()
    }

    @Test
    fun testIncrement() {
        sut.increment()
        val count = sut.getValue()
        assertEquals(1, count)
    }

    @Test
    fun testReset() {
        sut.increment()
        sut.increment()
        sut.reset()
        val count = sut.getValue()
        assertEquals(0, count)
    }
}
