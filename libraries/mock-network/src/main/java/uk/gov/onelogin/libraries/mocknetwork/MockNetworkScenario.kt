package uk.gov.onelogin.libraries.mocknetwork

import io.ktor.http.HttpStatusCode

/**
 * A predefined network scenario used by [MockNetworkEngineFactory] to decide how the mocked
 * network layer responds.
 *
 * @property statusCode the HTTP status every mocked request responds with.
 * @property responseBody the response body every mocked request responds with.
 */
enum class MockNetworkScenario(val statusCode: HttpStatusCode, val responseBody: String) {
    /** Request succeeds with an empty JSON body. */
    EMPTY_SUCCESS(HttpStatusCode.OK, "{}"),

    /** Request fails with a server error. */
    SERVER_ERROR(HttpStatusCode.InternalServerError, "{}"),
}
