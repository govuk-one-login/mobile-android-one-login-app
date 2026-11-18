package uk.gov.onelogin.libraries.mocknetwork

/**
 * A network request made by the app, as seen by a [MockNetworkScenario].
 *
 * @property method the HTTP method, e.g. `GET`.
 * @property url the full request URL.
 * @property headers the request headers.
 */
data class MockNetworkRequest(
    val method: String,
    val url: String,
    val headers: Map<String, List<String>> = emptyMap(),
)

/**
 * The response a [MockNetworkScenario] returns for a [MockNetworkRequest].
 *
 * @property statusCode the HTTP status code.
 * @property body the response body.
 * @property contentType the value of the `Content-Type` response header.
 */
data class MockNetworkResponse(
    val statusCode: Int = 200,
    val body: String = "{}",
    val contentType: String = "application/json",
)
