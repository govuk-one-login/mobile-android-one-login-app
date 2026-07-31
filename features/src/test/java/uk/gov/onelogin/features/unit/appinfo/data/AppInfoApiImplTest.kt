import android.content.Context
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.network.api.v2.ApiRequest
import uk.gov.android.network.api.v3.ApiResponse
import uk.gov.android.network.client.v2.GenericHttpResponse
import uk.gov.android.network.client.v2.StubHttpClient
import uk.gov.android.network.client.v2.TestResponseException
import uk.gov.android.network.service.v2.DefaultNetworkService
import uk.gov.android.network.service.v2.NetworkService
import uk.gov.onelogin.features.appinfo.data.model.AppInfoData
import uk.gov.onelogin.features.appinfo.domain.AppInfoApiImpl

class AppInfoApiImplTest {
    private val context: Context = mock()
    private val httpClient = StubHttpClient()
    private val networkService: NetworkService = DefaultNetworkService(httpClient)
    private val apiResponse =
        ClassLoader
            .getSystemResource("api/appInfoResponseValue.json")
            .readText()
    private val data =
        ApiResponse.Success(
            200,
            AppInfoData(
                apps =
                    AppInfoData.App(
                        AppInfoData.AppInfo(
                            minimumVersion = "0.0.0",
                            available = true,
                            featureFlags = AppInfoData.FeatureFlags(true)
                        )
                    )
            )
        )
    private val exception = TestResponseException.internalServerError
    private lateinit var request: ApiRequest

    private val sut =
        AppInfoApiImpl(
            context,
            networkService,
        )

    @BeforeEach
    fun setup() {
        request =
            ApiRequest.Get(
                url = "https://mobile.build.account.gov.uk/appInfo",
                headers =
                    listOf(
                        "Cache-Control" to "no-store",
                        "Content-Type" to "application/json",
                        "Strict-Transport-Security" to "max-age=31536000",
                        "X-Content-Type-Options" to "nosniff",
                        "X-Frame-Options" to "DENY"
                    )
            )

        whenever(context.getString(any())).thenReturn("/appInfo")
        whenever(context.getString(any(), eq("/appInfo"))).thenAnswer {
            "https://mobile.build.account.gov.uk/appInfo"
        }
    }

    @Test
    fun `app info call successful`(): Unit =
        runTest {
            httpClient.response = GenericHttpResponse(200, apiResponse)
            val result = sut.callApi()
            assertEquals(data, result)
        }

    @Test
    fun `app info call fail`() =
        runTest {
            httpClient.exception = exception
            val result = sut.callApi()
            assertInstanceOf<ApiResponse.Failure<*, *>>(result)
            assertEquals(result.status, 500)
        }
}
