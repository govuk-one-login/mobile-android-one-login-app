package uk.gov.onelogin.optin.domain

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class SingleChoiceTest {
    private lateinit var choice: SingleChoice

    @BeforeTest
    fun setUp() {
        choice = SingleChoice()
    }

    @Test
    fun `option block is called`() = runTest {
        // Given a SingleChoice
        var actual = false
        // When calling choose
        choice.choose { actual = true }
        // Then the options code block is called
        assertEquals(expected = true, actual)
    }

    @Test
    fun `option block is not called a second time`() = runTest {
        // Given a SingleChoice
        var actual = false
        // When calling choose twice
        choice.choose {}
        choice.choose { actual = true }
        // Then the second options code block is not called
        assertEquals(expected = false, actual)
    }
}
