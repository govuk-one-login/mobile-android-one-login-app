package uk.gov.onelogin.libraries.mocknetwork.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkConfig
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkScenario
import uk.gov.onelogin.libraries.mocknetwork.ui.MockNetworkViewModel

class MockNetworkViewModelTest {
    private val config = MockNetworkConfig(initialScenario = MockNetworkScenario.EMPTY_SUCCESS)
    private val viewModel = MockNetworkViewModel(config)

    @Test
    fun `exposes the current scenario from the config`() {
        assertEquals(MockNetworkScenario.EMPTY_SUCCESS, viewModel.scenario.value)
    }

    @Test
    fun `selecting a scenario updates the config`() {
        viewModel.selectScenario(MockNetworkScenario.SERVER_ERROR)

        assertEquals(MockNetworkScenario.SERVER_ERROR, config.scenario.value)
    }

    @Test
    fun `exposed scenario follows changes made to the config`() {
        config.setScenario(MockNetworkScenario.SERVER_ERROR)

        assertEquals(MockNetworkScenario.SERVER_ERROR, viewModel.scenario.value)
    }
}
