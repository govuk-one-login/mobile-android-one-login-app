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
 */
object MockNetworkEngineFactory {
    fun create(config: MockNetworkConfig): HttpClientEngine = MockEngine { request ->
        val response = config.scenario.value.respond(
            MockNetworkRequest(
                method = request.method.value,
                url = request.url.toString(),
                headers = request.headers.toMap(),
            ),
        )
        respond(
            content = response.body,
            status = HttpStatusCode.fromValue(response.statusCode),
            headers = headersOf(HttpHeaders.ContentType, response.contentType),
        )
    }
}
