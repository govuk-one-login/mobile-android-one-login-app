package uk.gov.onelogin.network.usecase

import android.content.Context
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.client.StubHttpClient
import uk.gov.android.onelogin.R

class HelloWorldApiCallTest {
    private val mockContext: Context = mock()
    private lateinit var stubHttpClient: GenericHttpClient
    private lateinit var helloWorldService: HelloWorldApiCall

    @BeforeEach
    fun setup() {
        whenever(mockContext.getString(R.string.helloWorldEndpoint))
            .thenReturn("/hello-world")
        whenever(mockContext.getString(R.string.helloWorldUrl, "/hello-world"))
            .thenReturn("hello-world.com")
    }

    @Test
    fun `successful call returns hello world text`() = runTest {
        setupHelloWorldService(ApiResponse.Success("Hello World!"))
        val response = helloWorldService.happyPath()

        assertEquals("Hello World!", response)
    }

    @Test
    fun `error call returns error message`() = runTest {
        setupHelloWorldService(ApiResponse.Failure(status = 400, Exception("Bad")))
        val response = helloWorldService.happyPath()
        assertEquals("Bad", response)
    }

    private fun setupHelloWorldService(httpResponse: ApiResponse) {
        stubHttpClient = StubHttpClient(httpResponse)
        helloWorldService = HelloWorldApiCallImpl(mockContext, stubHttpClient)
    }
}
