package uk.gov.onelogin.libraries.mocknetwork.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkConfig
import uk.gov.onelogin.libraries.mocknetwork.TestMockNetworkScenarios
import uk.gov.onelogin.libraries.mocknetwork.ui.MockNetworkViewModel

class MockNetworkViewModelTest {
    private val config = MockNetworkConfig(TestMockNetworkScenarios.all)
    private val viewModel = MockNetworkViewModel(config)

    @Test
    fun `exposes the available scenarios from the config`() {
        assertEquals(TestMockNetworkScenarios.all, viewModel.scenarios)
    }

    @Test
    fun `exposes the current scenario from the config`() {
        assertEquals(TestMockNetworkScenarios.success, viewModel.scenario.value)
    }

    @Test
    fun `selecting a scenario updates the config`() {
        viewModel.selectScenario(TestMockNetworkScenarios.serverError)

        assertEquals(TestMockNetworkScenarios.serverError, config.scenario.value)
    }

    @Test
    fun `exposed scenario follows changes made to the config`() {
        config.setScenario(TestMockNetworkScenarios.serverError)

        assertEquals(TestMockNetworkScenarios.serverError, viewModel.scenario.value)
    }
}
