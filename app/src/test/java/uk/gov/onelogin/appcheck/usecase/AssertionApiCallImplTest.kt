package uk.gov.onelogin.appcheck.usecase

import android.content.Context
import java.io.IOException
import kotlin.test.Test
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.onelogin.developer.appcheck.usecase.AssertionApiCall
import uk.gov.onelogin.developer.appcheck.usecase.AssertionApiCallImpl

class AssertionApiCallImplTest {

    private lateinit var context: Context
    private lateinit var httpClient: GenericHttpClient
    private lateinit var assertionApiCall: AssertionApiCall

    @BeforeEach
    fun setUp() {
        context = mock()
        httpClient = mock()
        assertionApiCall = AssertionApiCallImpl(context, httpClient)

        whenever(context.getString(any())).thenReturn("/endpoint")
        whenever(context.getString(any(), eq("/endpoint"))).thenAnswer { "www.testUrl.com" }
    }

    @Test
    fun `invoke() - Success`() = runBlocking {
        val firebaseToken = "testToken"
        val expectedRequest = ApiRequest.Get(
            url = "www.testUrl.com?device=android",
            headers = listOf(
                "X-Firebase-Token" to firebaseToken
            )
        )
        whenever(httpClient.makeRequest(expectedRequest)).thenReturn(ApiResponse.Success("Success"))

        val result = assertionApiCall.invoke(firebaseToken)

        assertEquals("Success", result)
    }

    @Test
    fun `invoke() - Failure with error message`() = runBlocking {
        val firebaseToken = "testToken"
        val errorMessage = "Test error message"
        whenever(httpClient.makeRequest(any()))
            .thenReturn(ApiResponse.Failure(500, IOException(errorMessage)))

        val result = assertionApiCall.invoke(firebaseToken)

        assertEquals(errorMessage, result)
    }

    @Test
    fun `invoke() - Failure without error message`() = runBlocking {
        val firebaseToken = "testToken"
        whenever(httpClient.makeRequest(any()))
            .thenReturn(ApiResponse.Failure(500, IOException()))

        val result = assertionApiCall.invoke(firebaseToken)

        assertEquals("Error", result)
    }
}
