package uk.gov.onelogin.network

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkRequest

class MockNetworkScenariosTest {

    private fun get(url: String) = MockNetworkScenarios.default.respond(
        MockNetworkRequest(method = "GET", url = url),
    )

    @Test
    fun `the app info request is answered`() {
        val response = get("https://mobile.mock.account.gov.uk/appInfo")

        assertEquals(OK, response.statusCode)
    }

    @Test
    fun `requests no feature answers are reported as not mocked`() {
        val response = get("https://token.mock.account.gov.uk/token")

        assertEquals(NOT_IMPLEMENTED, response.statusCode)
        assertTrue(response.body.contains("Not mocked"))
    }

    private companion object {
        const val OK = 200
        const val NOT_IMPLEMENTED = 501
    }
}
