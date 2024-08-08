package uk.gov.onelogin

import android.os.Build
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.client.KtorHttpClient
import uk.gov.android.network.useragent.UserAgent
import uk.gov.android.network.useragent.UserAgentGeneratorImpl
import uk.gov.android.onelogin.BuildConfig


@ExtendWith(PactConsumerTestExt::class)
internal class GetActorIT {
    val userAgentGenerator = UserAgentGeneratorImpl().apply {
        setUserAgent(
            UserAgent(
                appName = "One Login",
                versionName = BuildConfig.VERSION_NAME,
                clientName = BuildConfig.APPLICATION_ID,
                manufacturer = "Android Studio",
                model = "Unit test",
                sdkVersion = Build.VERSION.SDK_INT,
                clientVersion = BuildConfig.VERSION_NAME
            )
        )
    }
    val client: GenericHttpClient = KtorHttpClient(userAgentGenerator)
    var headers: MutableMap<String, String> = HashMap()

    @BeforeEach
    fun setUp() {

    }

    @Pact(provider = PACT_PROVIDER, consumer = PACT_CONSUMER)
    fun createPact(builder: PactDslWithProvider): RequestResponsePact {
        headers["Accept"] = "application/json"
        headers["Accept-Charset"] = "UTF-8"
        headers["User-Agent"] = "One Login/1.0 Android Studio/Unit test Android/0 uk.gov.onelogin.build/1.0"

        val bodyReturned = PactDslJsonBody()
            .uuid("id", "1bfff94a-b70e-4b39-bd2a-be1c0f898589")
            .stringType("name", "Shakira")
            .stringType("associatedPinyinSound", "Shi")
            .stringType("family", "Female I sound")
            .stringType("imageUrl", "http://anyimage.com")
            .close()

        return builder
            .given("A request to retrieve an actor")
            .uponReceiving("A request to retrieve an actor")
            .path("/1bfff94a-b70e-4b39-bd2a-be1c0f898589")
            .method("GET")
            .headers(headers)
            .willRespondWith()
            .status(200)
            .body(bodyReturned!!)
            .toPact()
    }

    @Test
    @PactTestFor(providerName = PACT_PROVIDER, port = PACT_PORT, pactVersion = PactSpecVersion.V3)
    fun runTestt() = runTest {

        //Mock url
        val apiRequest = ApiRequest.Get(MOCK_PACT_URL + "/1bfff94a-b70e-4b39-bd2a-be1c0f898589")

        val response: ApiResponse = client.makeRequest(apiRequest)


        Assert.assertTrue(response is ApiResponse.Success<*>)
    }

    companion object {
        const val PACT_PORT: String = "8888"
        const val MOCK_PACT_URL: String = "http://localhost:" + PACT_PORT
        const val BASE_PACT_URL_LOCAL: String = "localhost"
        const val BASE_URI_LOCAL: String = "http://localhost:"
        const val PACT_PROVIDER: String = "MY_PROVIDER"
        const val PACT_CONSUMER: String = "MY_CONSUMER"
    }
}