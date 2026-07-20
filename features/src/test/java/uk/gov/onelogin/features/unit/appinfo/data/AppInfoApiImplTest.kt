package uk.gov.onelogin.features.unit.appinfo.data

import android.content.Context
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.network.client.v2.GenericHttpResponse
import uk.gov.android.network.client.v2.GenericResponseException
import uk.gov.android.network.client.v2.StubHttpClient
import uk.gov.android.network.service.DefaultNetworkService
import uk.gov.onelogin.features.appinfo.data.model.AppInfoData
import uk.gov.onelogin.features.appinfo.domain.AppInfoApiImpl
import uk.gov.onelogin.features.appinfo.domain.AppInfoApiResult
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AppInfoApiImplTest {
    private val context: Context = mock()
    private val stubHttpClient = StubHttpClient()
    private val networkService = DefaultNetworkService(stubHttpClient)
    private val apiResponse =
        ClassLoader
            .getSystemResource("api/appInfoResponseValue.json")
            .readText()
    private val expectedData =
        AppInfoData(
            apps =
                AppInfoData.App(
                    AppInfoData.AppInfo(
                        minimumVersion = "0.0.0",
                        available = true,
                        featureFlags = AppInfoData.FeatureFlags(true),
                    ),
                ),
        )

    private val sut =
        AppInfoApiImpl(
            context,
            networkService,
        )

    @BeforeEach
    fun setup() {
        whenever(context.getString(any())).thenReturn("/appInfo")
        whenever(context.getString(any(), eq("/appInfo"))).thenAnswer {
            "https://mobile.build.account.gov.uk/appInfo"
        }
    }

    @Test
    fun `app info call successful`() =
        runTest {
            stubHttpClient.response = GenericHttpResponse(200, apiResponse)

            val result = sut.callApi()

            assertIs<AppInfoApiResult.Success>(result)
            assertEquals(expectedData, result.data)
        }

    @Test
    fun `app info call fail`() =
        runTest {
            stubHttpClient.exception = GenericResponseException(
                GenericHttpResponse(500, "error"),
                IllegalStateException(),
            )

            val result = sut.callApi()

            assertIs<AppInfoApiResult.Failure>(result)
            assertEquals(500, result.status)
        }

    @Test
    fun `app info call transport failure`() =
        runTest {
            stubHttpClient.exception = IOException()

            val result = sut.callApi()

            assertIs<AppInfoApiResult.Failure>(result)
        }
}
