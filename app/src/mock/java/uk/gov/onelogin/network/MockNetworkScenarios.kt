package uk.gov.onelogin.network

import uk.gov.onelogin.libraries.mocknetwork.MockNetworkRequest
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkResponse
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkScenario

/**
 * The mocked network responses available to the `mock` flavour.
 *
 * Scenarios live here, rather than in the mock library, because the responses depend on the
 * endpoints the app's features call.
 */
object MockNetworkScenarios {
    private const val APP_INFO_PATH = "/appInfo"

    private val APP_INFO_RESPONSE = MockNetworkResponse(
        body = """
            {
              "apps": {
                "android": {
                  "minimumVersion": "1.24.0",
                  "releaseFlags": {
                    "walletVisibleViaDeepLink": true,
                    "walletVisibleIfExists": true,
                    "walletVisibleToAll": true
                  },
                  "available": true,
                  "featureFlags": {
                    "appCheckEnabled": true
                  }
                },
                "iOS": {
                  "minimumVersion": "1.3.0",
                  "releaseFlags": {
                    "walletVisibleViaDeepLink": true,
                    "walletVisibleIfExists": true,
                    "walletVisibleToAll": true
                  },
                  "available": true,
                  "featureFlags": {
                    "appCheckEnabled": true
                  }
                }
              }
            }
        """.trimIndent(),
    )

    /**
     * Answers the app info request, which the splash screen must receive before the user can get
     * any further. Any other request is reported as not mocked.
     */
    val default = MockNetworkScenario("Default") { request ->
        when {
            request.url.contains(APP_INFO_PATH) -> APP_INFO_RESPONSE
            else -> notMocked(request)
        }
    }

    val all = listOf(default)

    private fun notMocked(request: MockNetworkRequest) = MockNetworkResponse(
        statusCode = NOT_IMPLEMENTED,
        body = """{"error":"Not mocked: ${request.method} ${request.url}"}""",
    )

    private const val NOT_IMPLEMENTED = 501
}
