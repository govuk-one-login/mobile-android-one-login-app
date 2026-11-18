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
 * @param scenarios the scenarios that can be selected. Must not be empty, and their names must be
 * unique.
 * @param initialScenario the scenario selected initially. Defaults to the first of [scenarios].
 */
class MockNetworkConfig(
    val scenarios: List<MockNetworkScenario>,
    initialScenario: MockNetworkScenario =
        requireNotNull(scenarios.firstOrNull()) { "At least one scenario is required" },
) {
    init {
        require(scenarios.map { it.name }.distinct().size == scenarios.size) {
            "Scenario names must be unique"
        }
        require(initialScenario in scenarios) {
            "The initial scenario must be one of the available scenarios"
        }
    }

    private val _scenario = MutableStateFlow(initialScenario)
    val scenario: StateFlow<MockNetworkScenario> = _scenario.asStateFlow()

    fun setScenario(scenario: MockNetworkScenario) {
        require(scenario in scenarios) {
            "The scenario must be one of the available scenarios"
        }
        _scenario.value = scenario
    }
}
