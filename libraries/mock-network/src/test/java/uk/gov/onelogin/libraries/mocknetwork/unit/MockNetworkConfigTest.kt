package uk.gov.onelogin.libraries.mocknetwork.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkConfig
import uk.gov.onelogin.libraries.mocknetwork.TestMockNetworkScenarios

class MockNetworkConfigTest {
    private val config = MockNetworkConfig(TestMockNetworkScenarios.all)

    @Test
    fun `exposes the available scenarios`() {
        assertEquals(TestMockNetworkScenarios.all, config.scenarios)
    }

    @Test
    fun `selects the first scenario initially`() {
        assertEquals(TestMockNetworkScenarios.success, config.scenario.value)
    }

    @Test
    fun `updates the current scenario`() {
        config.setScenario(TestMockNetworkScenarios.serverError)

        assertEquals(TestMockNetworkScenarios.serverError, config.scenario.value)
    }

    @Test
    fun `has no scenarios and none selected when given an empty list`() {
        val empty = MockNetworkConfig(emptyList())

        assertTrue(empty.scenarios.isEmpty())
        assertNull(empty.scenario.value)
    }

    @Test
    fun `has no scenarios and none selected by default`() {
        val empty = MockNetworkConfig()

        assertTrue(empty.scenarios.isEmpty())
        assertNull(empty.scenario.value)
    }
}
