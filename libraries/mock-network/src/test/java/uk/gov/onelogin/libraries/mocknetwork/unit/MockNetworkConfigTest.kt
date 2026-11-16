package uk.gov.onelogin.libraries.mocknetwork.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkConfig
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkScenario

class MockNetworkConfigTest {

    @Test
    fun `defaults to empty success scenario`() {
        val config = MockNetworkConfig()

        assertEquals(MockNetworkScenario.EMPTY_SUCCESS, config.scenario.value)
    }

    @Test
    fun `honours an explicit initial scenario`() {
        val config = MockNetworkConfig(initialScenario = MockNetworkScenario.SERVER_ERROR)

        assertEquals(MockNetworkScenario.SERVER_ERROR, config.scenario.value)
    }

    @Test
    fun `updates the current scenario`() {
        val config = MockNetworkConfig()

        config.setScenario(MockNetworkScenario.SERVER_ERROR)

        assertEquals(MockNetworkScenario.SERVER_ERROR, config.scenario.value)
    }
}
