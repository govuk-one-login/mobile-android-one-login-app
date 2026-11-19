package uk.gov.onelogin.libraries.mocknetwork

/**
 * Scenarios for tests. In the app these are defined by the app module.
 */
object TestMockNetworkScenarios {
    val success = MockNetworkScenario("Success") { MockNetworkResponse() }

    val serverError = MockNetworkScenario("Server error") {
        MockNetworkResponse(statusCode = 500)
    }

    val all = listOf(success, serverError)
}
