package uk.gov.onelogin.network

import uk.gov.onelogin.features.appinfo.MockAppInfo
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkRequest
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkResponse
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkScenario

/**
 * The mocked network responses available to the `mock` flavour.
 *
 * Each feature owns the response to its own endpoints, so a scenario is assembled here from those
 * responses. Anything left unanswered is reported as not mocked, rather than being made to look
 * like a success.
 */
object MockNetworkScenarios {
    private const val NOT_IMPLEMENTED = 501

    val default = MockNetworkScenario("Default") { request ->
        MockAppInfo.respondTo(request)
            ?: notMocked(request)
    }

    val all = listOf(default)

    private fun notMocked(request: MockNetworkRequest) = MockNetworkResponse(
        statusCode = NOT_IMPLEMENTED,
        body = """{"error":"Not mocked: ${request.method} ${request.url}"}""",
    )
}
