package uk.gov.onelogin.features.appinfo.data

import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.logging.api.Logger
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.tokens.data.ApiInfoException
import uk.gov.onelogin.features.TestUtils
import uk.gov.onelogin.features.appinfo.data.model.AppInfoLocalState
import uk.gov.onelogin.features.appinfo.data.model.AppInfoRemoteState
import uk.gov.onelogin.features.appinfo.data.model.AppInfoServiceState
import uk.gov.onelogin.features.appinfo.domain.AppInfoLocalSource
import uk.gov.onelogin.features.appinfo.domain.AppInfoRemoteSource
import uk.gov.onelogin.features.appinfo.domain.AppInfoService
import uk.gov.onelogin.features.appinfo.domain.AppInfoServiceImpl
import uk.gov.onelogin.features.appinfo.domain.AppVersionCheck
import uk.gov.onelogin.features.featureflags.domain.FeatureFlagSetter

class AppInfoServiceImplTest {
    private val remoteSource: AppInfoRemoteSource = mock()
    private val localSource: AppInfoLocalSource = mock()
    private val appVersionCheck: AppVersionCheck = mock()
    private val featureFlagSetter: FeatureFlagSetter = mock()
    private lateinit var logger: Logger
    private val data = TestUtils.appInfoData
    private val dataAppUnavailable = TestUtils.appInfoDataAppUnavailable
    private val localSourceErrorMsg = AppInfoLocalSourceImpl.APP_INFO_LOCAL_SOURCE_ERROR
    private val remoteSourceErrorMsg = AppInfoRemoteSourceImpl.APP_INFO_REMOTE_SOURCE_ERROR

    private lateinit var sut: AppInfoService

    fun setup() {
        sut = AppInfoServiceImpl(
            remoteSource,
            localSource,
            appVersionCheck,
            featureFlagSetter,
            logger
        )
    }

    @Test
    fun `successful remote retrieval`() =
        runTest {
            logger = SystemLogger()
            setup()
            whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Success(data))
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(data))
            whenever(appVersionCheck.compareVersions(eq(data)))
                .thenReturn(AppInfoServiceState.Successful(data))
            val result = sut.get()
            assertEquals(AppInfoServiceState.Successful(data), result)
            assertTrue((logger as SystemLogger).size == 0)
            verify(featureFlagSetter).setFromAppInfo(data.apps.android)
        }

    @Test
    fun `device offline - successful local retrieval`() =
        runTest {
            logger = SystemLogger()
            setup()
            whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Offline)
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(data))
            whenever(appVersionCheck.compareVersions(eq(data)))
                .thenReturn(AppInfoServiceState.Successful(data))
            val result = sut.get()
            assertEquals(AppInfoServiceState.Successful(data), result)
            assertTrue((logger as SystemLogger).size == 0)
            verify(featureFlagSetter).setFromAppInfo(data.apps.android)
        }

    @Test
    fun `device offline - failed local retrieval`() =
        runTest {
            logger = SystemLogger()
            setup()
            whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Offline)
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Failure("Error"))
            val result = sut.get()
            assertEquals(AppInfoServiceState.Offline, result)
            assertTrue((logger as SystemLogger).size == 0)
            verifyNoInteractions(featureFlagSetter)
        }

    @Test
    fun `failed remote - successful local retrieval`() =
        runTest {
            logger = mock()
            setup()
            whenever(remoteSource.get()).thenReturn(
                AppInfoRemoteState.Failure(remoteSourceErrorMsg)
            )
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(data))
            whenever(appVersionCheck.compareVersions(eq(data)))
                .thenReturn(AppInfoServiceState.Successful(data))
            val result = sut.get()
            assertEquals(AppInfoServiceState.Successful(data), result)
            verify(logger).error(
                eq(ApiInfoException::class.simpleName.toString()),
                eq(remoteSourceErrorMsg),
                any()
            )
            verify(featureFlagSetter).setFromAppInfo(data.apps.android)
        }

    @Test
    fun `failed remote and local retrieval`() =
        runTest {
            logger = mock()
            setup()
            whenever(remoteSource.get()).thenReturn(
                AppInfoRemoteState.Failure(remoteSourceErrorMsg)
            )
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Failure(localSourceErrorMsg))
            val result = sut.get()
            assertEquals(AppInfoServiceState.Unavailable, result)
            verify(logger).error(
                eq(ApiInfoException::class.simpleName.toString()),
                eq(remoteSourceErrorMsg),
                any()
            )
            verifyNoInteractions(featureFlagSetter)
        }

    @Test
    fun `update required error`() =
        runTest {
            logger = SystemLogger()
            setup()
            whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Success(data))
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(data))
            whenever(appVersionCheck.compareVersions(eq(data)))
                .thenReturn(AppInfoServiceState.UpdateRequired)
            val result = sut.get()
            assertEquals(AppInfoServiceState.UpdateRequired, result)
            assertTrue((logger as SystemLogger).size == 0)
            verify(featureFlagSetter).setFromAppInfo(data.apps.android)
        }

    @Test
    fun `available set to false - unavailable `() =
        runTest {
            logger = SystemLogger()
            setup()
            whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Success(dataAppUnavailable))
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(dataAppUnavailable))
            val result = sut.get()
            assertEquals(AppInfoServiceState.Unavailable, result)
            assertTrue((logger as SystemLogger).size == 0)
            verify(featureFlagSetter).setFromAppInfo(dataAppUnavailable.apps.android)
        }
}
