package uk.gov.onelogin

import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
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
import uk.gov.onelogin.login.usecase.extractEmailFromIdToken

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
    fun createPact_NoScope(builder: PactDslWithProvider): RequestResponsePact {
        val bodyReturned = PactDslJsonBody()
            .stringType("access_token", "mockAccessToken")
            .stringValue("token_type", "Bearer")
            .integerType("expires_in", 180)
            .close()

        return builder
            .given("mock.auth.code is a valid authorization code")
            .given(
                "https://mock-redirect-uri.gov.uk is the redirect URI used in the " +
                    "authorization request"
            )
            .given(
                "the code_challenge sent in the authorization request matches the " +
                    "verifier mock_code_verifier"
            )
            .uponReceiving(
                "a valid access token request with no previously requested " +
                    "scope"
            )
            .method("POST")
            .path("/token")
            .body(tokenQueries.toQueryStrings())
            .headers(commonHeaders)
            .willRespondWith()
            .status(200)
            .headers(
                mapOf(
                    "Content-Type" to "application/json"
                )
            )
            .body(bodyReturned!!)
            .toPact()
    }

    @Pact(provider = PACT_PROVIDER, consumer = PACT_CONSUMER)
    fun createPact_OpenIdScope(builder: PactDslWithProvider): RequestResponsePact {
        val bodyReturned = PactDslJsonBody()
            .stringType("access_token", "mockAccessToken")
            .stringMatcher(
                "id_token",
                "^(.+)\\.(.+)\\.(.+)$",
                "mockHeader.$jsonIdTokenPayloadEncoded.mockSignature"
            )
            .stringValue("token_type", "Bearer")
            .integerType("expires_in", 180)
            .close()

        return builder
            .given("mock.auth.code is a valid authorization code")
            .given(
                "https://mock-redirect-uri.gov.uk is the redirect URI used in the " +
                    "authorization request"
            )
            .given(
                "the code_challenge sent in the authorization request matches the " +
                    "verifier mock_code_verifier"
            )
            .given("a previously requested scope of openid")
            .uponReceiving(
                "a valid access token request with a previously requested " +
                    "scope of openid"
            )
            .method("POST")
            .path("/token")
            .body(tokenQueries.toQueryStrings())
            .headers(commonHeaders)
            .willRespondWith()
            .status(200)
            .headers(
                mapOf(
                    "Content-Type" to "application/json"
                )
            )
            .body(bodyReturned!!)
            .toPact()
    }

    @Pact(provider = PACT_PROVIDER, consumer = PACT_CONSUMER)
    fun createPact_Authorization(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .given(
                "there is a registered client with id mock_client_id, with " +
                    "https://mock-redirect-uri.gov.uk as a registered redirect URI, with " +
                    "openid as a registered scope, with code as a registered response type"
            )
            .uponReceiving("A valid authorization request")
            .method("GET")
            .path("/authorize")
            .query(authorizationQueries.toQueryStrings())
            .headers(mapOf(UserAgentHeader))
            .willRespondWith()
            .status(200)
            .toPact()
    }

    @Test
    @PactTestFor(
        pactMethod = "createPact_NoScope",
        providerName = PACT_PROVIDER,
        port = PACT_PORT,
        pactVersion = PactSpecVersion.V3
    )
    fun testAccessTokenRequestWithNoPreviouslyRequestedScope() = runTest {
        val apiRequest = ApiRequest.FormUrlEncoded(MOCK_PACT_URL + "/token", tokenQueries.toList())

        val response: ApiResponse = networkClient.makeRequest(apiRequest)

        Assertions.assertTrue(response is ApiResponse.Success<*>)
    }

    @Test
    @PactTestFor(
        pactMethod = "createPact_OpenIdScope",
        providerName = PACT_PROVIDER,
        port = PACT_PORT,
        pactVersion = PactSpecVersion.V3
    )
    fun testAccessTokenRequestWithPreviouslyRequestedScopeOfOpenId() = runTest {
        val apiRequest = ApiRequest.FormUrlEncoded(MOCK_PACT_URL + "/token", tokenQueries.toList())
        val response: ApiResponse = networkClient.makeRequest(apiRequest)
        Assertions.assertTrue(response is ApiResponse.Success<*>)

        val stringResponse = (response as ApiResponse.Success<String>).response

        Assertions.assertEquals("email@example.com", stringResponse.extractEmailFromIdToken())
    }

    @Test
    @PactTestFor(
        pactMethod = "createPact_Authorization",
        providerName = PACT_PROVIDER,
        port = PACT_PORT,
        pactVersion = PactSpecVersion.V3
    )
    fun testValidAuthorizationRequest() = runTest {
        val apiRequest =
            ApiRequest.Get(MOCK_PACT_URL + "/authorize?" + authorizationQueries.toQueryStrings())
        val response: ApiResponse = networkClient.makeRequest(apiRequest)
        Assertions.assertTrue(response is ApiResponse.Success<*>)
    }

    companion object {
        const val PACT_PORT: String = "8888"
        const val MOCK_PACT_URL: String = "http://localhost:" + PACT_PORT
        const val PACT_PROVIDER: String = "Mobile.MobilePlatform.StsBackendApi"
        const val PACT_CONSUMER: String = "Mobile.MobilePlatform.OneLoginAppAndroid"

        val ContentTypeFormUrlHeader = "Content-Type" to "application/x-www-form-urlencoded"
        val UserAgentHeader =
            "User-Agent" to "One Login App Android/1.0 JVM/Unit test Android/1 uk.gov.onelogin/1.0"
        val commonHeaders = mapOf(
            ContentTypeFormUrlHeader,
            UserAgentHeader
        )

        val tokenQueries = mapOf(
            "code" to "mock.auth.code",
            "code_verifier" to "mock_code_verifier",
            "redirect_uri" to "https://mock-redirect-uri.gov.uk",
            "grant_type" to "authorization_code"
        )
        val authorizationQueries = mapOf(
            "client_id" to "mock_client_id",
            "redirect_uri" to "https://mock-redirect-uri.gov.uk",
            "state" to "mock_state",
            "nonce" to "mock_nonce",
            "scope" to "openid",
            "response_type" to "code",
            "code_challenge_method" to "S256",
            "code_challenge" to "mock_code_challenge"
        )

        fun Map<String, String>.toQueryStrings() = this.map { "${it.key}=${it.value}" }
            .joinToString("&")

        val jsonIdTokenPayload =
            """
                {
                    "email": "email@example.com",
                    "email_verified": true,
                    "persistent_id": "mock_persistent_id"
                }
            """.trimIndent()

        @OptIn(ExperimentalEncodingApi::class)
        val jsonIdTokenPayloadEncoded = Base64.encode(jsonIdTokenPayload.toByteArray())
    }
}
