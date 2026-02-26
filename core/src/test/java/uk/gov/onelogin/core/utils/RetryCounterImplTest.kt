package uk.gov.onelogin.core.utils

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RetryCounterImplTest {
    private lateinit var sut: Counter

    @BeforeEach
    fun setup() {
        sut = RetryCounter()
    }

    @Test
    fun `test increment and get count`() {
        var actual = 0

        assertEquals(0, actual)

        sut.incrementCount()
        actual = sut.getCount()

        assertEquals(1, actual)

        sut.incrementCount()
        sut.incrementCount()
        sut.incrementCount()
        actual = sut.getCount()

        assertEquals(4, actual)
    }

    @Test
    fun `test reset count`() {
        var actual = 0

        assertEquals(0, actual)

        sut.incrementCount()
        sut.incrementCount()
        actual = sut.getCount()

        assertEquals(2, actual)

        sut.reset()

        actual = sut.getCount()

        assertEquals(0, actual)
    }
}
