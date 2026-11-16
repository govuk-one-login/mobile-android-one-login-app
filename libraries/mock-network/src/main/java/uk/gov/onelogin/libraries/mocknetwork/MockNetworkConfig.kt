package uk.gov.onelogin.libraries.mocknetwork

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Holds the currently selected [MockNetworkScenario].
 *
 * This is the single source of truth that the mocked network engine reads on each request and
 * that the runtime configuration UI mutates, so that automated (e.g. Maestro) tests can change
 * the mocked network behaviour on the fly. It is intended to be a singleton within the mocked
 * build.
 */
class MockNetworkConfig(initialScenario: MockNetworkScenario = MockNetworkScenario.EMPTY_SUCCESS) {
    private val _scenario = MutableStateFlow(initialScenario)
    val scenario: StateFlow<MockNetworkScenario> = _scenario.asStateFlow()

    fun setScenario(scenario: MockNetworkScenario) {
        _scenario.value = scenario
    }
}
