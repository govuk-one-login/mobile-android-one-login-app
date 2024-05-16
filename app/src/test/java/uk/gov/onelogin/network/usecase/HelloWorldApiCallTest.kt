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
    private lateinit var usecase: HelloWorldApiCall

    @BeforeEach
    fun setup() {
        whenever(mockContext.getString(R.string.helloWorldEndpoint))
            .thenReturn("/hello-world")
        whenever(mockContext.getString(R.string.helloWorldUrl, "/hello-world"))
            .thenReturn("hello-world.com")
    }

    @Test
    fun `successful call returns hello world text`() = runTest {
        setupUsecase(ApiResponse.Success("Hello World!"))
        val response = usecase()

        assertEquals("Hello World!", response)
    }

    private fun setupUsecase(httpResponse: ApiResponse) {
        stubHttpClient = StubHttpClient(httpResponse)
        usecase = HelloWorldApiCallImpl(mockContext, stubHttpClient)
    }
}
