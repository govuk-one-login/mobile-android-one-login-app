package uk.gov.onelogin.network.usecase

import android.content.Context
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.network.api.ApiFailureReason
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.client.StubHttpClient
import uk.gov.android.onelogin.R
import uk.gov.onelogin.network.data.AuthApiResponse

class HelloWorldApiCallTest {
    private val mockContext: Context = mock()
    private lateinit var stubHttpClient: GenericHttpClient
    private lateinit var helloWorldService: HelloWorldApiCall

    @BeforeEach
    fun setup() {
        whenever(mockContext.getString(R.string.helloWorldEndpoint))
            .thenReturn("/hello-world")
        whenever(mockContext.getString(eq(R.string.helloWorldUrl), any()))
            .thenReturn("hello-world.com")
    }

    @Test
    fun `happy path successful call returns hello world text`() = runTest {
        setupHelloWorldService(ApiResponse.Success("Hello World!"))
        val response = helloWorldService.happyPath()

        assertThat("response is success", response is AuthApiResponse.Success<*>)
        assertEquals("Hello World!", (response as AuthApiResponse.Success<*>).response)
    }

    @Test
    fun `happy path error call returns error message`() = runTest {
        setupHelloWorldService(
            ApiResponse.Failure(
                ApiFailureReason.General, 400, Exception("Bad")
            )
        )
        val response = helloWorldService.happyPath()
        assertThat("response is failure", response is AuthApiResponse.Failure)
        assertEquals("Bad", (response as AuthApiResponse.Failure).e.message)
    }

    @Test
    fun `happy path error call returns error with no message`() = runTest {
        setupHelloWorldService(
            ApiResponse.Failure(
                ApiFailureReason.Non200Response,
                400,
                Exception()
            )
        )
        val response = helloWorldService.happyPath()
        assertThat("response is success", response is AuthApiResponse.Failure)
        assertEquals("Error", (response as AuthApiResponse.Failure).e.message)
    }

    @Test
    fun `access token expired call returns Auth expired`() = runTest {
        setupHelloWorldService(
            ApiResponse.Failure(
                ApiFailureReason.AccessTokenExpired,
                0,
                Exception()
            )
        )
        val response = helloWorldService.errorPath()

        assertThat("response is auth expired", response is AuthApiResponse.AuthExpired)
    }

    @Test
    fun `error path error call returns error message`() = runTest {
        setupHelloWorldService(
            ApiResponse.Failure(
                ApiFailureReason.AuthFailed,
                400,
                Exception("Bad")
            )
        )
        val response = helloWorldService.errorPath()
        assertThat("response is failure", response is AuthApiResponse.Failure)
        assertEquals("Bad", (response as AuthApiResponse.Failure).e.message)
    }

    private fun setupHelloWorldService(httpResponse: ApiResponse) {
        stubHttpClient = StubHttpClient(httpResponse)
        helloWorldService = HelloWorldApiCallImpl(mockContext, stubHttpClient)
    }
}
