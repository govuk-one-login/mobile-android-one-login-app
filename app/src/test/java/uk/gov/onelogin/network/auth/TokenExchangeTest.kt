package uk.gov.onelogin.network.auth

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.headersOf
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.onelogin.network.auth.TokenExchange.Companion.TokenExchangeCodeArgError
import uk.gov.onelogin.network.auth.TokenExchange.Companion.TokenExchangeOfflineError
import uk.gov.onelogin.network.auth.TokenExchange.Companion.TokenExchangeServerError
import uk.gov.onelogin.network.utils.HttpClientStub
import uk.gov.onelogin.network.utils.HttpClientStub.Companion.HttpClientStubResponse
import uk.gov.onelogin.network.utils.OnlineCheckerStub

class TokenExchangeTest {
    private val code = "aCode"
    private val httpClient = HttpClientStub()
    private val redirectUrl = Url("https://example.com/redirect")
    private val stubOnlineChecker = OnlineCheckerStub()
    private val url = Url("https://example.com/token")

    @Before
    fun setup() {
        stubOnlineChecker.online = true
    }

    @After
    fun tearDown() {
        assertEquals(
            "No mocked http calls should remain",
            0,
            httpClient.callsRemaining()
        )
    }

    @Test(expected = TokenExchangeCodeArgError::class)
    fun `throws a TokenExchangeCodeArgError when an empty code is passed`() {
        TokenExchange(
            code = "",
            onlineChecker = stubOnlineChecker,
            redirectUrl = redirectUrl,
            url = url,
            httpClient = httpClient.client
        )
    }

    @Test(expected = TokenExchangeOfflineError::class)
    fun `throws a TokenExchangeOfflineError when the device is offline`() {
        stubOnlineChecker.online = false

        TokenExchange(
            code = code,
            onlineChecker = stubOnlineChecker,
            redirectUrl = redirectUrl,
            url = url,
            httpClient = httpClient.client
        )
    }

    @Test(expected = TokenExchangeServerError::class)
    fun `throws a TokenExchangeServerError when a 500 range error is received`() = runBlocking {
        val expectedUrl = URLBuilder(url).apply {
            parameters.apply {
                append("grant_type", "authorization_code" )
                append("code", code)
                append("redirect_uri", redirectUrl.toString())
            }
        }.build()

        val response = HttpClientStubResponse(
            content = "",
            headers = headersOf(
                HttpHeaders.ContentType,
                ContentType.Application.Json.toString()
            ),
            status = HttpStatusCode.InternalServerError
        )

        httpClient.addResponse(
            url = expectedUrl,
            response = response
        )

        TokenExchange(
            code = code,
            onlineChecker = stubOnlineChecker,
            redirectUrl = redirectUrl,
            url = url,
            httpClient = httpClient.client
        ).send()
    }
}
