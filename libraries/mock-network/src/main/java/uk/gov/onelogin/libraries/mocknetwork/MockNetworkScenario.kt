package uk.gov.onelogin.libraries.mocknetwork

/**
 * A named way for the mocked network layer to respond to requests.
 *
 * Scenarios are defined by the app module, as the concrete responses depend on the features that
 * are making the requests.
 *
 * @property name a unique, human readable name. It is shown on the configuration screen and is
 * used in the test tag of the scenario's button.
 * @property respond produces the response for each [MockNetworkRequest].
 */
class MockNetworkScenario(
    val name: String,
    val respond: (MockNetworkRequest) -> MockNetworkResponse,
)
