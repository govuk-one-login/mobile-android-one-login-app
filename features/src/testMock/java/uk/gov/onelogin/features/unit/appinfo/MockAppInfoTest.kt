package uk.gov.onelogin.features.unit.appinfo

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import uk.gov.onelogin.features.appinfo.MockAppInfo
import uk.gov.onelogin.features.appinfo.data.model.AppInfoData
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkRequest

class MockAppInfoTest {
    private val json = Json { ignoreUnknownKeys = true }

    private fun get(url: String) = MockAppInfo.respondTo(
        MockNetworkRequest(method = "GET", url = url),
    )

    @Test
    fun `answers the app info request`() {
        val response = get("https://mobile.mock.account.gov.uk/appInfo")

        assertEquals(OK, response?.statusCode)
    }

    @Test
    fun `responds with a body the app can deserialise`() {
        val response = get("https://mobile.mock.account.gov.uk/appInfo")

        val appInfo = json.decodeFromString<AppInfoData>(response!!.body).apps.android

        assertEquals("1.24.0", appInfo.minimumVersion)
        assertTrue(appInfo.available)
        assertTrue(appInfo.featureFlags.appCheckEnabled)
    }

    @Test
    fun `does not answer other requests`() {
        assertNull(get("https://token.mock.account.gov.uk/token"))
    }

    private companion object {
        const val OK = 200
    }
}
