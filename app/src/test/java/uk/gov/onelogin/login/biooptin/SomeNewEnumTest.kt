package uk.gov.onelogin.login.biooptin

import kotlin.test.Test
import kotlin.test.assertEquals

class SomeNewEnumTest {

    @Test
    fun new() {
        val preference = SomeNewEnum.New
        assertEquals(expected = preference, actual = SomeNewEnum.valueOf("New"))
    }

    @Test
    fun old() {
        val preference = SomeNewEnum.Old
        assertEquals(expected = preference, actual = SomeNewEnum.valueOf("Old"))
    }

    @Test
    fun some() {
        val preference = SomeNewEnum.Some
        assertEquals(expected = preference, actual = SomeNewEnum.valueOf("Some"))
    }
}
