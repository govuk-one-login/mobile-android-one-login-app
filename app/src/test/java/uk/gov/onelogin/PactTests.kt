package uk.gov.onelogin

import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.client.KtorHttpClient
import uk.gov.android.network.useragent.UserAgent
import uk.gov.android.network.useragent.UserAgentGeneratorImpl

@ExtendWith(PactConsumerTestExt::class)
class PactTests {
    val userAgentGenerator = UserAgentGeneratorImpl().apply {
        setUserAgent(
            UserAgent(
                appName = "One Login App Android",
                versionName = "1.0",
                clientName = "uk.gov.onelogin",
                manufacturer = "JVM",
                model = "Unit test",
                sdkVersion = 1,
                clientVersion = "1.0"
            )
        )
    }
    val networkClient: GenericHttpClient = KtorHttpClient(userAgentGenerator)

    @Pact(provider = PACT_PROVIDER, consumer = PACT_CONSUMER)
    fun createPact(builder: PactDslWithProvider): RequestResponsePact {
        val headers = mapOf(
            "Content-Type" to "application/x-www-form-urlencoded",
            "User-Agent" to "One Login App Android/1.0 JVM/Unit test Android/1 uk.gov.onelogin/1.0"
        )

        val bodyReturned = PactDslJsonBody()
            .stringType("access_token", "mockAccessToken")
            .stringType("token_type", "Bearer")
            .integerType("expires_in", 180)
            .close()

        return builder
            .given("mock.auth.code is a valid authorization code")
            .given("https://mock-redirect-uri.gov.uk is the redirect URI used in the authorization request")
            .given("the code_challenge sent in the authorization request matches the verifier mock_code_verifier")
            .uponReceiving("a valid access token request with no previously requested scope")
            .method("POST")
            .path("/token")
            .body(queryString)
            .headers(headers)
            .willRespondWith()
            .status(200)
            .headers(
                mapOf(
                    "Content-Type" to "application/json",
                )
            )
            .body(bodyReturned!!)
            .toPact()
    }

    @Test
    @PactTestFor(providerName = PACT_PROVIDER, port = PACT_PORT, pactVersion = PactSpecVersion.V3)
    fun testAccessTokenRequestWithNoPreviouslyRequestedScope() = runTest {

        //Mock url
        val apiRequest = ApiRequest.FormUrlEncoded(MOCK_PACT_URL + "/token", tokenQueries.toList())

        val response: ApiResponse = networkClient.makeRequest(apiRequest)

        Assertions.assertTrue(response is ApiResponse.Success<*>)
    }

    companion object {
        const val PACT_PORT: String = "8888"
        const val MOCK_PACT_URL: String = "http://localhost:" + PACT_PORT
        const val BASE_PACT_URL_LOCAL: String = "localhost"
        const val BASE_URI_LOCAL: String = "http://localhost:"
        const val PACT_PROVIDER: String = "Mobile.MobilePlatform.OneLoginAppAndroid"
        const val PACT_CONSUMER: String = "Mobile.MobilePlatform.StsBackendApi"

        val tokenQueries = mapOf(
            "code" to "mock.auth.code",
            "code_verifier" to "mock_code_verifier",
            "redirect_uri" to "https://mock-redirect-uri.gov.uk",
            "grant_type" to "authorization_code"
        )
        val queryString = tokenQueries.map { "${it.key}=${it.value}" }
            .joinToString("&")
    }
}