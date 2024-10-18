package uk.gov.onelogin.optin.domain

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
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

    @Test
    fun `initial state is PreChoice`() = runTest {
        // Given a SingleChoice
        val actual = choice.state.first()
        // Then initial state is PreChoice
        assertEquals(expected = SingleChoice.State.PreChoice, actual)
    }

    @Test
    fun `state after choice is PostChoice`() = runTest {
        // Given a SingleChoice
        // When choosing
        choice.choose { }
        val actual = choice.state.first()
        // Then the state is PostChoice
        assertEquals(expected = SingleChoice.State.PostChoice, actual)
    }
}
