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
import uk.gov.onelogin.network.auth.AuthCodeExchange.Companion.AuthCodeExchangeCodeArgError
import uk.gov.onelogin.network.auth.AuthCodeExchange.Companion.AuthCodeExchangeOfflineError
import uk.gov.onelogin.network.auth.AuthCodeExchange.Companion.AuthCodeExchangeServerError
import uk.gov.onelogin.network.utils.HttpClientStub
import uk.gov.onelogin.network.utils.HttpClientStub.Companion.HttpClientStubResponse
import uk.gov.onelogin.network.utils.OnlineCheckerStub

class AuthCodeExchangeTest {
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

    @Test(expected = AuthCodeExchangeCodeArgError::class)
    fun `throws a TokenExchangeCodeArgError when an empty code is passed`() {
        AuthCodeExchange(
            code = "",
            onlineChecker = stubOnlineChecker,
            redirectUrl = redirectUrl,
            url = url,
            httpClient = httpClient.client
        )
    }

    @Test(expected = AuthCodeExchangeOfflineError::class)
    fun `throws a TokenExchangeOfflineError when the device is offline`() {
        stubOnlineChecker.online = false

        AuthCodeExchange(
            code = code,
            onlineChecker = stubOnlineChecker,
            redirectUrl = redirectUrl,
            url = url,
            httpClient = httpClient.client
        )
    }

    @Test(expected = AuthCodeExchangeServerError::class)
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

        AuthCodeExchange(
            code = code,
            onlineChecker = stubOnlineChecker,
            redirectUrl = redirectUrl,
            url = url,
            httpClient = httpClient.client
        ).send()
    }
}
