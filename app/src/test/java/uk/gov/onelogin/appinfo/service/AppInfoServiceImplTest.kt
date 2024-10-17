package uk.gov.onelogin.appinfo.service

import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.TestUtils
import uk.gov.onelogin.appinfo.appversioncheck.domain.AppVersionCheck
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
    private val appVersionCheck: AppVersionCheck = mock()
    private val data = TestUtils.appInfoData
    private val localSourceErrorMsg = AppInfoLocalSourceImpl.APP_INFO_LOCAL_SOURCE_ERROR
    private val remoteSourceErrorMsg = AppInfoRemoteSourceImpl.APP_INFO_REMOTE_SOURCE_ERROR

    private val sut = AppInfoServiceImpl(
        remoteSource,
        localSource,
        appVersionCheck
    )

    @Test
    fun `successful remote retrieval`() = runTest {
        whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Success(data))
        whenever(appVersionCheck.compareVersions(eq(data)))
            .thenReturn(AppInfoServiceState.Successful(data))
        val result = sut.get()
        assertEquals(AppInfoServiceState.Successful(data), result)
    }

    @Test
    fun `device offline - successful local retrieval`() = runTest {
        whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Offline)
        whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(data))
        val result = sut.get()
        assertEquals(AppInfoServiceState.Successful(data), result)
    }

    @Test
    fun `device offline - failed local retrieval`() = runTest {
        whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Offline)
        whenever(localSource.get()).thenReturn(AppInfoLocalState.Failure("Error"))
        val result = sut.get()
        assertEquals(AppInfoServiceState.Offline, result)
    }

    @Test
    fun `failed remote - successful local retrieval`() = runTest {
        whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Failure(remoteSourceErrorMsg))
        whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(data))
        val result = sut.get()
        assertEquals(AppInfoServiceState.Successful(data), result)
    }

    @Test
    fun `failed remote and local retrieval`() = runTest {
        whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Failure(remoteSourceErrorMsg))
        whenever(localSource.get()).thenReturn(AppInfoLocalState.Failure(localSourceErrorMsg))
        val result = sut.get()
        assertEquals(AppInfoServiceState.Unavailable, result)
    }

    @Test
    fun `update required error`() = runTest {
        whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Success(data))
        whenever(appVersionCheck.compareVersions(eq(data)))
            .thenReturn(AppInfoServiceState.UpdateRequired)
        val result = sut.get()
        assertEquals(AppInfoServiceState.UpdateRequired, result)
    }
}
