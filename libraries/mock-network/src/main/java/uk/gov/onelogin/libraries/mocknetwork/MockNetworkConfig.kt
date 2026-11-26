package uk.gov.onelogin.libraries.mocknetwork

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Holds the available [MockNetworkScenario]s and the currently selected one.
 *
 * This is the single source of truth that the mocked network engine reads on each request and
 * that the runtime configuration UI mutates, so that automated (e.g. Maestro) tests can change
 * the mocked network behaviour on the fly. It is intended to be a singleton within the mocked
 * build.
 *
 * @param scenarios the scenarios that can be selected. The first is selected initially. May be
 * empty, in which case no scenario is selected and the configuration screen offers none.
 */
class MockNetworkConfig(val scenarios: List<MockNetworkScenario> = emptyList()) {
    private val _scenario = MutableStateFlow(scenarios.firstOrNull())

    /**
     * The selected scenario, or `null` if there is none to select.
     */
    val scenario: StateFlow<MockNetworkScenario?> = _scenario.asStateFlow()

    fun setScenario(scenario: MockNetworkScenario) {
        _scenario.value = scenario
    }
}
