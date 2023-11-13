package uk.gov.onelogin.network.auth

import android.content.Context
import android.content.res.Resources
import com.google.gson.Gson
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.R
import uk.gov.onelogin.network.auth.AuthCodeExchange.Companion.AuthCodeExchangeClientError
import uk.gov.onelogin.network.auth.AuthCodeExchange.Companion.AuthCodeExchangeCodeArgError
import uk.gov.onelogin.network.auth.AuthCodeExchange.Companion.AuthCodeExchangeOfflineError
import uk.gov.onelogin.network.auth.AuthCodeExchange.Companion.AuthCodeExchangeServerError
import uk.gov.onelogin.network.auth.AuthCodeExchange.Companion.AuthCodeExchangeUnexpectedResponse
import uk.gov.onelogin.network.auth.response.TokenResponse
import uk.gov.onelogin.network.utils.HttpClientStub
import uk.gov.onelogin.network.utils.HttpClientStub.Companion.HttpClientStubResponse
import uk.gov.onelogin.network.utils.OnlineCheckerStub

class AuthCodeExchangeTest {
    private val context: Context = mock()
    private val resources: Resources = mock()
    private val code = "aCode"
    private val httpClient = HttpClientStub()
    private val redirectUrl = Url("https://example.com/redirect")
    private val onlineChecker = OnlineCheckerStub()
    private val url = Url("https://api.example.com/token")

    private lateinit var authCodeExchange: AuthCodeExchange

    @BeforeEach
    fun setup() {
        onlineChecker.online = true

        whenever(context.resources).thenReturn(resources)
        whenever(resources.getString(R.string.webRedirectEndpoint)).thenReturn(redirectUrl.fullPath)
        whenever(resources.getString(R.string.tokenExchangeEndpoint)).thenReturn(url.fullPath)
        whenever(resources.getString(eq(R.string.webBaseUrl), any())).then {
            with(redirectUrl) {
                "${protocol.name}://${host}${it.getArgument<String>(1)}"
            }
        }
        whenever(resources.getString(eq(R.string.apiBaseUrl), any())).then {
            with(url) {
                "${protocol.name}://${host}${it.getArgument<String>(1)}"
            }
        }

        authCodeExchange = AuthCodeExchange(
            context = context,
            httpClient = httpClient,
            onlineChecker = onlineChecker
        )
    }

    @AfterEach
    fun tearDown() {
        assertEquals(0, httpClient.callsRemaining(), "No mocked http calls should remain")
    }

    @Test
    fun `throws a AuthCodeCodeArgError when an empty code is passed`() {
        val thrown = assertThrows(AuthCodeExchangeCodeArgError::class.java) {
            runBlocking {
                authCodeExchange.exchangeCode("")
            }
        }

        assertEquals("Code should be a non-empty string", thrown.message)
    }

    @Test
    fun `throws a AuthCodeOfflineError when the device is offline`() {
        onlineChecker.online = false

        val thrown = assertThrows(AuthCodeExchangeOfflineError::class.java) {
            runBlocking {
                authCodeExchange.exchangeCode(code = code)
            }
        }

        assertEquals("The device appears to be offline", thrown.message)
    }

    @Test
    fun `throws a AuthCodeServerError when a 500 range error is received`() {
        val response = HttpClientStubResponse(
            content = "Internal Server Error",
            headers = headersOf(
                HttpHeaders.ContentType,
                ContentType.Application.Json.toString()
            ),
            status = HttpStatusCode.InternalServerError
        )

        httpClient.addResponse(
            url = url,
            response = response
        )

        val thrown = assertThrows(AuthCodeExchangeServerError::class.java) {
            runBlocking {
                authCodeExchange.exchangeCode(code = code)
            }
        }

        assertEquals(
            "Server Error received - 500 Internal Server Error - Internal Server Error",
            thrown.message
        )
    }

    @Test
    fun `throws a AuthCodeClientError when a 400 range error is received`() {
        val response = HttpClientStubResponse(
            content = "Bad Request",
            headers = headersOf(
                HttpHeaders.ContentType,
                ContentType.Application.Json.toString()
            ),
            status = HttpStatusCode.BadRequest
        )

        httpClient.addResponse(
            url = url,
            response = response
        )

        val thrown = assertThrows(AuthCodeExchangeClientError::class.java) {
            runBlocking {
                authCodeExchange.exchangeCode(code = code)
            }
        }

        assertEquals("Client Error received - 400 Bad Request - Bad Request", thrown.message)
    }

    @Test
    fun `throws a AuthCodeExchangeUnexpectedResponse when a 300 range response is received`() {
        val response = HttpClientStubResponse(
            content = "Multiple Choices",
            headers = headersOf(
                HttpHeaders.ContentType,
                ContentType.Application.Json.toString()
            ),
            status = HttpStatusCode.MultipleChoices
        )

        httpClient.addResponse(
            url = url,
            response = response
        )

        val thrown = assertThrows(AuthCodeExchangeUnexpectedResponse::class.java) {
            runBlocking {
                authCodeExchange.exchangeCode(code = code)
            }
        }

        assertEquals(
            "Unexpected response received - 300 Multiple Choices - Multiple Choices",
            thrown.message
        )
    }

    @Test
    fun `throws a AuthCodeExchangeUnexpectedResponse when a non-200 response is received`() {
        val response = HttpClientStubResponse(
            content = "Created",
            headers = headersOf(
                HttpHeaders.ContentType,
                ContentType.Application.Json.toString()
            ),
            status = HttpStatusCode.OK
        )

        httpClient.addResponse(
            url = url,
            response = response
        )

        val thrown = assertThrows(AuthCodeExchangeUnexpectedResponse::class.java) {
            runBlocking {
                authCodeExchange.exchangeCode(code = code)
            }
        }

        assertEquals("Unexpected response received - 201 Created - Created", thrown.message)
    }

    @Test
    fun `returns a TokenResponse when a 200 response is received`() {
        val tokenResponse = TokenResponse(
            access = "accessToken",
            expires = 180,
            id = "idToken",
            refresh = "refreshToken",
            scope = "scope",
            type = "type"
        )

        val response = HttpClientStubResponse(
            content = Gson().toJson(tokenResponse),
            headers = headersOf(
                HttpHeaders.ContentType,
                ContentType.Application.Json.toString()
            ),
            status = HttpStatusCode.OK
        )

        httpClient.addResponse(
            url = url,
            response = response
        )

        val tokens: TokenResponse

        runBlocking {
            tokens = authCodeExchange.exchangeCode(code = code)
        }

        assertEquals(tokens, tokenResponse)
    }
}
