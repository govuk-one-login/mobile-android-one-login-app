package uk.gov.onelogin.features.unit.appinfo.data

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import uk.gov.android.network.online.OnlineChecker
import uk.gov.onelogin.features.appinfo.data.AppInfoRemoteSourceImpl
import uk.gov.onelogin.features.appinfo.data.AppInfoRemoteSourceImpl.Companion.APP_INFO_REMOTE_SOURCE_ERROR
import uk.gov.onelogin.features.appinfo.data.model.AppInfoData
import uk.gov.onelogin.features.appinfo.data.model.AppInfoRemoteState
import uk.gov.onelogin.features.appinfo.domain.AppInfoApi
import uk.gov.onelogin.features.appinfo.domain.AppInfoApiResult
import kotlin.test.assertEquals

class AppInfoRemoteSourceImplTest {
    private val appInfoApi: AppInfoApi = mock()
    private val onlineChecker: OnlineChecker = mock()
    private val data =
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

    private val sut = AppInfoRemoteSourceImpl(appInfoApi, onlineChecker)

    @BeforeEach
    fun setup() {
        whenever(onlineChecker.isOnline()).thenReturn(true)
    }

    @Test
    fun `successful api call`() =
        runTest {
            whenever(appInfoApi.callApi()).thenReturn(AppInfoApiResult.Success(data))

            val result = sut.get()

            assertEquals(AppInfoRemoteState.Success(data), result)
        }

    @Test
    fun `offline`() =
        runTest {
            whenever(onlineChecker.isOnline()).thenReturn(false)

            val result = sut.get()

            assertEquals(AppInfoRemoteState.Offline, result)
        }

    @Test
    fun `failed api call`() =
        runTest {
            val error = Exception("Server error")
            whenever(appInfoApi.callApi()).thenReturn(AppInfoApiResult.Failure(500, error))

            val result = sut.get()

            assertEquals(
                AppInfoRemoteState.Failure("$APP_INFO_REMOTE_SOURCE_ERROR: Status: 500", error),
                result,
            )
        }
}
