package uk.gov.onelogin.network

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import uk.gov.onelogin.features.appinfo.data.model.AppInfoData
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkRequest

class MockNetworkScenariosTest {
    private val json = Json { ignoreUnknownKeys = true }

    private fun get(url: String) = MockNetworkScenarios.default.respond(
        MockNetworkRequest(method = "GET", url = url),
    )

    @Test
    fun `app info request succeeds`() {
        val response = get("https://mobile.build.account.gov.uk/appInfo")

        assertEquals(OK, response.statusCode)
    }

    @Test
    fun `app info response deserialises into the app's model`() {
        val response = get("https://mobile.build.account.gov.uk/appInfo")

        val appInfo = json.decodeFromString<AppInfoData>(response.body).apps.android

        assertEquals("1.24.0", appInfo.minimumVersion)
        assertTrue(appInfo.available)
        assertTrue(appInfo.featureFlags.appCheckEnabled)
    }

    @Test
    fun `requests other than app info are reported as not mocked`() {
        val response = get("https://token.build.account.gov.uk/token")

        assertEquals(NOT_IMPLEMENTED, response.statusCode)
        assertTrue(response.body.contains("Not mocked"))
    }

    private companion object {
        const val OK = 200
        const val NOT_IMPLEMENTED = 501
    }
}
