package uk.gov.onelogin.core.network.domain

import android.content.Context
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.network.api.v2.ApiResponse
import uk.gov.android.network.client.v2.GenericHttpClient
import uk.gov.android.network.client.v2.StubHttpClient
import uk.gov.android.network.service.DefaultNetworkService
import uk.gov.android.onelogin.core.R
import uk.gov.android.network.attestation.TestClientAttestationProvider
import uk.gov.android.network.auth.TestAuthenticationProvider
import uk.gov.android.network.client.v2.GenericHttpResponse
import uk.gov.android.network.client.v2.GenericResponseException
import uk.gov.android.network.dpop.TestDPoPProvider
import uk.gov.android.network.service.ApiResponseException
import uk.gov.android.network.service.NetworkingException
import uk.gov.android.network.service.ServiceException
import uk.gov.android.network.service.TransportException
import kotlin.test.assertEquals

class HelloWorldApiCallTest {
    private val mockContext: Context = mock()
    private val stubHttpClient = StubHttpClient()
    private val networkService = DefaultNetworkService(stubHttpClient).apply {
        setAuthenticationProvider(TestAuthenticationProvider())
        setDPoPProvider(TestDPoPProvider())
        setClientAttestationProvider(TestClientAttestationProvider())
    }
    private val helloWorldService = HelloWorldApiCallImpl(mockContext, networkService)

    @BeforeEach
    fun setup() {
        whenever(mockContext.getString(R.string.helloWorldEndpoint))
            .thenReturn("/hello-world")
        whenever(mockContext.getString(eq(R.string.helloWorldUrl), any()))
            .thenReturn("hello-world.com")
    }

    @Test
    fun `authenticated request returns hello world text`() =
        runTest {
            givenSuccess()
            val response = helloWorldService.authenticatedRequest()

            assertEquals("Hello World!", response)
        }

    @Test
    fun `given api fails with message, authenticated request returns error message`() =
        runTest {
            givenApiFailure()
            val response = helloWorldService.authenticatedRequest()
            assertEquals("API responded with 400", response)
        }

    @Test
    fun `given api fails with no message, authenticated request call returns error with message`() =
        runTest {
            givenTransportFailure()
            val response = helloWorldService.authenticatedRequest()
            assertEquals("Error", response)
        }

    @Test
    fun `error request returns hello world text`() =
        runTest {
            givenSuccess()
            val response = helloWorldService.authenticatedErrorRequest()

            assertEquals("Hello World!", response)
        }

    @Test
    fun `given api fails, error request returns error message`() =
        runTest {
            givenApiFailure()
            val response = helloWorldService.authenticatedErrorRequest()
            assertEquals("API responded with 400", response)
        }

    @Test
    fun `given api fails with no message, error request returns error with message`() =
        runTest {
            givenTransportFailure()
            val response = helloWorldService.authenticatedErrorRequest()
            assertEquals("Error", response)
        }

    private fun givenSuccess() {
        stubHttpClient.response = GenericHttpResponse(200, "Hello World!")
    }

    private fun givenApiFailure() {
        stubHttpClient.exception = GenericResponseException(
            GenericHttpResponse( 400, "Failure"),
            IllegalStateException()
        )
    }
    private fun givenTransportFailure() {
        stubHttpClient.exception = IOException()
    }
}
