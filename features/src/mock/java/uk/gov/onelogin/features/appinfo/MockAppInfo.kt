package uk.gov.onelogin.features.appinfo

import uk.gov.onelogin.libraries.mocknetwork.MockNetworkRequest
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkResponse

/**
 * The mocked response to the app info request, which lives alongside the feature that makes it.
 *
 * The splash screen must receive this before the user can get any further, so every scenario that
 * goes beyond the splash screen needs to answer it. The app module decides which scenarios exist
 * and how anything unanswered is handled.
 */
object MockAppInfo {
    private const val PATH = "/appInfo"
    private const val OK = 200

    private val RESPONSE = MockNetworkResponse(
        statusCode = OK,
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
     * The response to the app info request, or `null` if [request] is for something else.
     */
    fun respondTo(request: MockNetworkRequest): MockNetworkResponse? =
        if (request.url.contains(PATH)) RESPONSE else null
}
