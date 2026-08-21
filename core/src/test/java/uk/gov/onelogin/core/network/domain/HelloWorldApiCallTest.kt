package uk.gov.onelogin.core.network.domain

import android.content.Context
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.network.service.v2.StubNetworkService
import uk.gov.android.onelogin.core.R

class HelloWorldApiCallTest {
    private val mockContext: Context = mock()
    private val stubNetworkService = StubNetworkService()
    private val helloWorldService = HelloWorldApiCallImpl(mockContext, stubNetworkService)

    @BeforeEach
    fun setup() {
        whenever(mockContext.getString(R.string.helloWorldEndpoint))
            .thenReturn("/hello-world")
        whenever(mockContext.getString(eq(R.string.helloWorldUrl), any()))
            .thenReturn("hello-world.com")
    }

    @Test
    fun `happy path successful call returns hello world text`() =
        runTest {
            stubNetworkService.setSuccessResponse(200, "Hello World!")
            val response = helloWorldService.happyPath()

            assertEquals("Hello World!", response)
        }

    @Test
    fun `happy path error call returns failure`() =
        runTest {
            stubNetworkService.setFailureResponse(400, "Bad")
            val response = helloWorldService.happyPath()
            assertThat(response, containsString("400"))
        }

    @Test
    fun `error path successful call returns hello world text`() =
        runTest {
            stubNetworkService.setSuccessResponse(200, "Hello World!")
            val response = helloWorldService.errorPath()

            assertEquals("Hello World!", response)
        }

    @Test
    fun `error path error call returns failure`() =
        runTest {
            stubNetworkService.setFailureResponse(400, "Bad")
            val response = helloWorldService.errorPath()
            assertThat(response, containsString("400"))
        }
}
