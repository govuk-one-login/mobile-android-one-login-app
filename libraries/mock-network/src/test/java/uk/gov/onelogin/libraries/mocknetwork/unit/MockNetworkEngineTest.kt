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
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkScenario

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
    fun `empty success scenario returns a 200 success`() = runTest {
        val config = MockNetworkConfig(initialScenario = MockNetworkScenario.EMPTY_SUCCESS)
        val service = networkService(config)

        val response = service.makeRequest(ApiRequest.Get("https://mock.example/"))

        val success = assertInstanceOf(ApiResponse.Success::class.java, response)
        assertEquals(200, success.status)
    }

    @Test
    fun `server error scenario returns a 500 failure`() = runTest {
        val config = MockNetworkConfig(initialScenario = MockNetworkScenario.SERVER_ERROR)
        val service = networkService(config)

        val response = service.makeRequest(ApiRequest.Get("https://mock.example/"))

        val failure = assertInstanceOf(ApiResponse.Failure::class.java, response)
        assertEquals(500, failure.status)
    }

    @Test
    fun `changing the scenario at runtime changes the response`() = runTest {
        val config = MockNetworkConfig(initialScenario = MockNetworkScenario.EMPTY_SUCCESS)
        val service = networkService(config)

        assertInstanceOf(
            ApiResponse.Success::class.java,
            service.makeRequest(ApiRequest.Get("https://mock.example/")),
        )

        config.setScenario(MockNetworkScenario.SERVER_ERROR)

        assertInstanceOf(
            ApiResponse.Failure::class.java,
            service.makeRequest(ApiRequest.Get("https://mock.example/")),
        )
    }
}
