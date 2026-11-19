package uk.gov.onelogin.libraries.mocknetwork

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.toMap

/**
 * Builds a Ktor [MockEngine] whose responses are produced by the currently selected
 * [MockNetworkScenario] held in [config].
 *
 * The scenario is read on every request, so changing it at runtime (via the configuration UI)
 * immediately affects subsequent requests without rebuilding the engine or the HTTP client.
 *
 * When no scenario is selected, requests are answered with [NO_SCENARIO_RESPONSE] rather than
 * reaching the network.
 */
object MockNetworkEngineFactory {
    /**
     * The response returned while no [MockNetworkScenario] is selected. `501 Not Implemented`
     * makes an unconfigured mock obvious, instead of resembling a successful response.
     */
    val NO_SCENARIO_RESPONSE = MockNetworkResponse(
        statusCode = 501,
        body = """{"error":"No mock network scenario selected"}""",
    )

    fun create(config: MockNetworkConfig): HttpClientEngine = MockEngine { request ->
        val scenario = config.scenario.value
        val response = scenario?.respond(
            MockNetworkRequest(
                method = request.method.value,
                url = request.url.toString(),
                headers = request.headers.toMap(),
            ),
        ) ?: NO_SCENARIO_RESPONSE

        respond(
            content = response.body,
            status = HttpStatusCode.fromValue(response.statusCode),
            headers = headersOf(HttpHeaders.ContentType, response.contentType),
        )
    }
}
