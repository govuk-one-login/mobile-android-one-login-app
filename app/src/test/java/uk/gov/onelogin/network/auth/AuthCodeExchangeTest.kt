package uk.gov.onelogin.network.auth

import android.content.Context
import android.content.res.Resources
import com.google.gson.Gson
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
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

    @Before
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

        authCodeExchange  = AuthCodeExchange(
            context = context,
            httpClient = httpClient,
            onlineChecker = onlineChecker
        )
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
    fun `throws a AuthCodeCodeArgError when an empty code is passed`() {
        runBlocking {
            authCodeExchange.exchangeCode("")
        }
    }

    @Test(expected = AuthCodeExchangeOfflineError::class)
    fun `throws a AuthCodeOfflineError when the device is offline`() {
        onlineChecker.online = false

        runBlocking {
            authCodeExchange.exchangeCode(code = code)
        }
    }

    @Test(expected = AuthCodeExchangeServerError::class)
    fun `throws a AuthCodeServerError when a 500 range error is received`() {
        val expectedUrl = URLBuilder(url).apply {
            parameters.apply {
                append("grant_type", "authorization_code")
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

        runBlocking {
            authCodeExchange.exchangeCode(code = code)
        }
    }

    @Test(expected = AuthCodeExchangeClientError::class)
    fun `throws a AuthCodeClientError when a 400 range error is received`() {
        val expectedUrl = URLBuilder(url).apply {
            parameters.apply {
                append("grant_type", "authorization_code")
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
            status = HttpStatusCode.BadRequest
        )

        httpClient.addResponse(
            url = expectedUrl,
            response = response
        )

        runBlocking {
            authCodeExchange.exchangeCode(code = code)
        }
    }

    @Test(expected = AuthCodeExchangeUnexpectedResponse::class)
    fun `throws a AuthCodeExchangeUnexpectedResponse when a 300 range response is received`() {
        val expectedUrl = URLBuilder(url).apply {
            parameters.apply {
                append("grant_type", "authorization_code")
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
            status = HttpStatusCode.MultipleChoices
        )

        httpClient.addResponse(
            url = expectedUrl,
            response = response
        )

        runBlocking {
            authCodeExchange.exchangeCode(code = code)
        }
    }

    @Test(expected = AuthCodeExchangeUnexpectedResponse::class)
    fun `throws a AuthCodeExchangeUnexpectedResponse when a non-200 response is received`() {
        val expectedUrl = URLBuilder(url).apply {
            parameters.apply {
                append("grant_type", "authorization_code")
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
            status = HttpStatusCode.Created
        )

        httpClient.addResponse(
            url = expectedUrl,
            response = response
        )

        runBlocking {
            authCodeExchange.exchangeCode(code = code)
        }
    }

    @Test
    fun `returns a TokenResponse when a 200 response is received`() {
        val expectedUrl = URLBuilder(url).apply {
            parameters.apply {
                append("grant_type", "authorization_code")
                append("code", code)
                append("redirect_uri", redirectUrl.toString())
            }
        }.build()

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
            url = expectedUrl,
            response = response
        )

        val tokens: TokenResponse

        runBlocking {
            tokens = authCodeExchange.exchangeCode(code = code)
        }

        assertEquals(tokens, tokenResponse)
    }
}