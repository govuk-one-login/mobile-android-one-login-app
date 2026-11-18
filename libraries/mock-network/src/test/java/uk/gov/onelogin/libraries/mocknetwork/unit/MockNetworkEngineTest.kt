package uk.gov.onelogin.libraries.mocknetwork.unit

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import uk.gov.android.network.api.v2.ApiRequest
import uk.gov.android.network.api.v3.ApiResponse
import uk.gov.android.network.service.v2.DefaultNetworkService
import uk.gov.android.network.useragent.UserAgent
import uk.gov.android.network.useragent.UserAgentGeneratorImpl
import uk.gov.onelogin.libraries.mocknetwork.MockKtorHttpClientFactory
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkConfig
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkRequest
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkResponse
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkScenario
import uk.gov.onelogin.libraries.mocknetwork.TestMockNetworkScenarios

class MockNetworkEngineTest {

    private fun networkService(config: MockNetworkConfig): DefaultNetworkService {
        val userAgentGenerator = UserAgentGeneratorImpl().apply {
            setUserAgent(
                UserAgent(
                    appName = "mock",
                    versionName = "1.0",
                    clientName = "mock",
                    manufacturer = "mock",
                    model = "mock",
                    sdkVersion = 0,
                    clientVersion = "1.0",
                ),
            )
        }
        return DefaultNetworkService(
            MockKtorHttpClientFactory.create(userAgentGenerator, config),
        )
    }

    @Test
    fun `success scenario returns a 200 success`() = runTest {
        val config = MockNetworkConfig(TestMockNetworkScenarios.all)
        val service = networkService(config)

        val response = service.makeRequest(ApiRequest.Get("https://mock.example/"))

        val success = assertInstanceOf(ApiResponse.Success::class.java, response)
        assertEquals(200, success.status)
    }

    @Test
    fun `server error scenario returns a 500 failure`() = runTest {
        val config = MockNetworkConfig(listOf(TestMockNetworkScenarios.serverError))
        val service = networkService(config)

        val response = service.makeRequest(ApiRequest.Get("https://mock.example/"))

        val failure = assertInstanceOf(ApiResponse.Failure::class.java, response)
        assertEquals(500, failure.status)
    }

    @Test
    fun `changing the scenario at runtime changes the response`() = runTest {
        val config = MockNetworkConfig(TestMockNetworkScenarios.all)
        val service = networkService(config)

        assertInstanceOf(
            ApiResponse.Success::class.java,
            service.makeRequest(ApiRequest.Get("https://mock.example/")),
        )

        config.setScenario(TestMockNetworkScenarios.serverError)

        assertInstanceOf(
            ApiResponse.Failure::class.java,
            service.makeRequest(ApiRequest.Get("https://mock.example/")),
        )
    }

    @Test
    fun `a scenario can respond differently depending on the request`() = runTest {
        val perUrl = MockNetworkScenario("Per URL") { request ->
            when {
                request.url.endsWith("/found") ->
                    MockNetworkResponse(body = """{"value":"found"}""")

                else -> MockNetworkResponse(statusCode = 404)
            }
        }
        val service = networkService(MockNetworkConfig(listOf(perUrl)))

        val found = service.makeRequest(ApiRequest.Get("https://mock.example/found"))
        val missing = service.makeRequest(ApiRequest.Get("https://mock.example/missing"))

        val success = assertInstanceOf(ApiResponse.Success::class.java, found)
        assertEquals("""{"value":"found"}""", success.body)
        val failure = assertInstanceOf(ApiResponse.Failure::class.java, missing)
        assertEquals(404, failure.status)
    }

    @Test
    fun `a scenario receives the details of the request`() = runTest {
        var received: MockNetworkRequest? = null
        val capturing = MockNetworkScenario("Capturing") { request ->
            received = request
            MockNetworkResponse()
        }
        val service = networkService(MockNetworkConfig(listOf(capturing)))

        service.makeRequest(
            ApiRequest.Get(
                url = "https://mock.example/path",
                headers = listOf("X-Test" to "value"),
            ),
        )

        assertEquals("GET", received?.method)
        assertEquals("https://mock.example/path", received?.url)
        assertEquals(listOf("value"), received?.headers?.get("X-Test"))
    }
}
