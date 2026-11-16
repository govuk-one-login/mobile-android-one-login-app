package uk.gov.onelogin.libraries.mocknetwork

import io.ktor.http.HttpStatusCode

/**
 * A predefined network scenario used by [MockNetworkEngineFactory] to decide how the mocked
 * network layer responds.
 *
 * Scenarios are intentionally coarse-grained for now: the initial mock journeys only need a
 * small, fixed set of behaviours. Finer-grained, per-endpoint configuration can be layered on
 * later without changing the injection point.
 *
 * @property statusCode the HTTP status every mocked request responds with.
 * @property responseBody the response body every mocked request responds with.
 */
enum class MockNetworkScenario(val statusCode: HttpStatusCode, val responseBody: String) {
    /** Every request succeeds with an empty JSON body. */
    EMPTY_SUCCESS(HttpStatusCode.OK, "{}"),

    /** Every request fails with a server error. */
    SERVER_ERROR(HttpStatusCode.InternalServerError, "{}"),
}
