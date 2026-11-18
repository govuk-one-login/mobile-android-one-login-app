package uk.gov.onelogin.libraries.mocknetwork.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkConfig
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkResponse
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkScenario
import uk.gov.onelogin.libraries.mocknetwork.TestMockNetworkScenarios

class MockNetworkConfigTest {

    @Test
    fun `exposes the available scenarios`() {
        val config = MockNetworkConfig(TestMockNetworkScenarios.all)

        assertEquals(TestMockNetworkScenarios.all, config.scenarios)
    }

    @Test
    fun `defaults to the first scenario`() {
        val config = MockNetworkConfig(TestMockNetworkScenarios.all)

        assertEquals(TestMockNetworkScenarios.success, config.scenario.value)
    }

    @Test
    fun `honours an explicit initial scenario`() {
        val config = MockNetworkConfig(
            scenarios = TestMockNetworkScenarios.all,
            initialScenario = TestMockNetworkScenarios.serverError,
        )

        assertEquals(TestMockNetworkScenarios.serverError, config.scenario.value)
    }

    @Test
    fun `updates the current scenario`() {
        val config = MockNetworkConfig(TestMockNetworkScenarios.all)

        config.setScenario(TestMockNetworkScenarios.serverError)

        assertEquals(TestMockNetworkScenarios.serverError, config.scenario.value)
    }

    @Test
    fun `rejects an empty list of scenarios`() {
        assertThrows(IllegalArgumentException::class.java) {
            MockNetworkConfig(emptyList())
        }
    }

    @Test
    fun `rejects duplicate scenario names`() {
        val duplicate = MockNetworkScenario(TestMockNetworkScenarios.success.name) {
            MockNetworkResponse()
        }

        assertThrows(IllegalArgumentException::class.java) {
            MockNetworkConfig(listOf(TestMockNetworkScenarios.success, duplicate))
        }
    }

    @Test
    fun `rejects an initial scenario that is not available`() {
        assertThrows(IllegalArgumentException::class.java) {
            MockNetworkConfig(
                scenarios = listOf(TestMockNetworkScenarios.success),
                initialScenario = TestMockNetworkScenarios.serverError,
            )
        }
    }

    @Test
    fun `rejects selecting a scenario that is not available`() {
        val config = MockNetworkConfig(listOf(TestMockNetworkScenarios.success))

        assertThrows(IllegalArgumentException::class.java) {
            config.setScenario(TestMockNetworkScenarios.serverError)
        }
    }
}
