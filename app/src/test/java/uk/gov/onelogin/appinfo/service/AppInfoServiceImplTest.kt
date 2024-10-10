package uk.gov.onelogin.appinfo.service

import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.service.data.AppInfoServiceImpl
import uk.gov.onelogin.appinfo.service.domain.model.AppInfoServiceState
import uk.gov.onelogin.appinfo.source.data.AppInfoLocalSourceImpl
import uk.gov.onelogin.appinfo.source.data.AppInfoRemoteSourceImpl
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoLocalState
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoRemoteState
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoRemoteSource

class AppInfoServiceImplTest {
    private val remoteSource: AppInfoRemoteSource = mock()
    private val localSource: AppInfoLocalSource = mock()
    private val data = AppInfoData(
        apps = AppInfoData.App(
            AppInfoData.AppInfo(
                minimumVersion = "0.0.0",
                releaseFlags = AppInfoData.ReleaseFlags(
                    true,
                    true,
                    true
                ),
                available = true,
                featureFlags = AppInfoData.FeatureFlags(true)
            )
        )
    )
    private val localSourceErrorMsg = AppInfoLocalSourceImpl.APP_INFO_LOCAL_SOURCE_ERROR
    private val remoteSourceErrorMsg = AppInfoRemoteSourceImpl.APP_INFO_REMOTE_SOURCE_ERROR

    private val sut = AppInfoServiceImpl(
        remoteSource,
        localSource
    )

    @Test
    fun `successful remote retrieval`() = runTest {
        whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Success(data))
        val result = sut.get()
        assertEquals(AppInfoServiceState.RemoteSuccess(data), result)
    }

    @Test
    fun `device offline - successful local retrieval`() = runTest {
        whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Offline)
        whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(data))
        val result = sut.get()
        assertEquals(AppInfoServiceState.LocalSuccess(data), result)
    }

    @Test
    fun `device offline - failed local retrieval`() = runTest {
        whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Offline)
        whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(data))
        val result = sut.get()
        assertEquals(AppInfoServiceState.LocalSuccess(data), result)
    }

    @Test
    fun `failed remote - successful local retrieval`() = runTest {
        whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Failure(remoteSourceErrorMsg))
        whenever(localSource.get()).thenReturn(AppInfoLocalState.Failure(localSourceErrorMsg))
        val result = sut.get()
        assertEquals(AppInfoServiceState.Unavailable, result)
    }

    @Test
    fun `failed remote and local retrieval`() = runTest {
        whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Failure(remoteSourceErrorMsg))
        whenever(localSource.get()).thenReturn(AppInfoLocalState.Failure(localSourceErrorMsg))
        val result = sut.get()
        assertEquals(AppInfoServiceState.Unavailable, result)
    }
}
