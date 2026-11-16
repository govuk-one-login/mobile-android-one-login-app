package uk.gov.onelogin.libraries.mocknetwork

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf

/**
 * Builds a Ktor [MockEngine] whose responses are driven by the current [MockNetworkScenario]
 * held in [config].
 *
 * The scenario is read on every request, so changing it at runtime (via the configuration UI)
 * immediately affects subsequent requests without rebuilding the engine or the HTTP client.
 */
object MockNetworkEngineFactory {
    fun create(config: MockNetworkConfig): HttpClientEngine = MockEngine {
        val scenario = config.scenario.value
        respond(
            content = scenario.responseBody,
            status = scenario.statusCode,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
    }
}
