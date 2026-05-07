package uk.gov.onelogin.features.unit.appinfo.data

import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.logging.api.v3.LogLevel
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasCustomKeys
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasException
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.isLogLevel
import uk.gov.logging.api.v3.matchers.MemorisedLoggerMatchers.hasSize
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
import uk.gov.onelogin.core.tokens.data.ApiInfoException
import uk.gov.onelogin.features.TestUtils
import uk.gov.onelogin.features.appinfo.data.AppInfoLocalSourceImpl
import uk.gov.onelogin.features.appinfo.data.AppInfoRemoteSourceImpl
import uk.gov.onelogin.features.appinfo.data.model.AppInfoLocalState
import uk.gov.onelogin.features.appinfo.data.model.AppInfoRemoteState
import uk.gov.onelogin.features.appinfo.data.model.AppInfoServiceState
import uk.gov.onelogin.features.appinfo.domain.AppInfoLocalSource
import uk.gov.onelogin.features.appinfo.domain.AppInfoRemoteSource
import uk.gov.onelogin.features.appinfo.domain.AppInfoService
import uk.gov.onelogin.features.appinfo.domain.AppInfoServiceImpl
import uk.gov.onelogin.features.appinfo.domain.AppVersionCheck
import uk.gov.onelogin.features.featureflags.domain.FeatureFlagSetter
import kotlin.test.assertEquals

class AppInfoServiceImplTest {
    private val remoteSource: AppInfoRemoteSource = mock()
    private val localSource: AppInfoLocalSource = mock()
    private val appVersionCheck: AppVersionCheck = mock()
    private val featureFlagSetter: FeatureFlagSetter = mock()
    private lateinit var logger: MemorisedLogger
    private val data = TestUtils.appInfoData
    private val dataAppUnavailable = TestUtils.appInfoDataAppUnavailable
    private val localSourceErrorMsg = AppInfoLocalSourceImpl.Companion.APP_INFO_LOCAL_SOURCE_ERROR
    private val remoteSourceErrorMsg = AppInfoRemoteSourceImpl.Companion.APP_INFO_REMOTE_SOURCE_ERROR
    private val errorKeyComponent = componentKey("app_info")
    private val errorKeyActionGetRemote = actionKey("Get remote app info")

    private lateinit var sut: AppInfoService

    fun setup() {
        sut =
            AppInfoServiceImpl(
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
            logger = MemorisedLogger()
            setup()
            whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Success(data))
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(data))
            whenever(appVersionCheck.compareVersions(eq(data)))
                .thenReturn(AppInfoServiceState.Successful(data))
            val result = sut.get()
            assertEquals(AppInfoServiceState.Successful(data), result)
            assertThat(logger, hasSize(0))
            verify(featureFlagSetter).setFromAppInfo(data.apps.android)
        }

    @Test
    fun `device offline - successful local retrieval`() =
        runTest {
            logger = MemorisedLogger()
            setup()
            whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Offline)
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(data))
            whenever(appVersionCheck.compareVersions(eq(data)))
                .thenReturn(AppInfoServiceState.Successful(data))
            val result = sut.get()
            assertEquals(AppInfoServiceState.Successful(data), result)
            assertThat(logger, hasSize(0))
            verify(featureFlagSetter).setFromAppInfo(data.apps.android)
        }

    @Test
    fun `device offline - failed local retrieval`() =
        runTest {
            logger = MemorisedLogger()
            setup()
            whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Offline)
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Failure("Error"))
            val result = sut.get()
            assertEquals(AppInfoServiceState.Offline, result)
            assertThat(logger, hasSize(0))
            verifyNoInteractions(featureFlagSetter)
        }

    @Test
    fun `failed remote with error - successful local retrieval`() =
        runTest {
            val err = ApiInfoException(Exception(remoteSourceErrorMsg))
            logger = MemorisedLogger()
            setup()
            whenever(remoteSource.get()).thenReturn(
                AppInfoRemoteState.Failure(err.message!!, err.exception)
            )
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(data))
            whenever(appVersionCheck.compareVersions(eq(data)))
                .thenReturn(AppInfoServiceState.Successful(data))
            val result = sut.get()
            assertEquals(AppInfoServiceState.Successful(data), result)
            assertThat(
                logger,
                hasItem(
                    allOf(
                        isLogLevel(LogLevel.Error),
                        hasMessage(remoteSourceErrorMsg),
                        hasException(equalTo(err)),
                        hasCustomKeys(
                            contains(
                                equalTo(errorKeyComponent),
                                equalTo(errorKeyActionGetRemote)
                            )
                        )
                    )
                )
            )
            verify(featureFlagSetter).setFromAppInfo(data.apps.android)
        }

    @Test
    fun `failed remote without error - successful local retrieval`() =
        runTest {
            val err = ApiInfoException(Exception(remoteSourceErrorMsg))
            logger = MemorisedLogger()
            setup()
            whenever(remoteSource.get()).thenReturn(
                AppInfoRemoteState.Failure(err.message!!)
            )
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(data))
            whenever(appVersionCheck.compareVersions(eq(data)))
                .thenReturn(AppInfoServiceState.Successful(data))
            val result = sut.get()
            assertEquals(AppInfoServiceState.Successful(data), result)
            assertThat(
                logger,
                hasItem(
                    allOf(
                        isLogLevel(LogLevel.Error),
                        hasMessage(remoteSourceErrorMsg),
                        not(hasException(equalTo(err))),
                        hasCustomKeys(
                            contains(
                                equalTo(errorKeyComponent),
                                equalTo(errorKeyActionGetRemote)
                            )
                        )
                    )
                )
            )
            verify(featureFlagSetter).setFromAppInfo(data.apps.android)
        }

    @Test
    fun `failed remote and local retrieval`() =
        runTest {
            val err = Exception(remoteSourceErrorMsg)
            val wrappedException = ApiInfoException(err)
            logger = MemorisedLogger()
            setup()
            whenever(remoteSource.get()).thenReturn(
                AppInfoRemoteState.Failure(err.message!!, err)
            )
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Failure(localSourceErrorMsg))
            val result = sut.get()
            assertEquals(AppInfoServiceState.Unavailable, result)
            assertThat(
                logger,
                hasItem(
                    allOf(
                        isLogLevel(LogLevel.Error),
                        hasMessage(remoteSourceErrorMsg),
                        hasException(equalTo(wrappedException)),
                        hasCustomKeys(
                            contains(
                                equalTo(errorKeyComponent),
                                equalTo(errorKeyActionGetRemote)
                            )
                        )
                    )
                )
            )
            verifyNoInteractions(featureFlagSetter)
        }

    @Test
    fun `update required error`() =
        runTest {
            logger = MemorisedLogger()
            setup()
            whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Success(data))
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(data))
            whenever(appVersionCheck.compareVersions(eq(data)))
                .thenReturn(AppInfoServiceState.UpdateRequired)
            val result = sut.get()
            assertEquals(AppInfoServiceState.UpdateRequired, result)
            assertThat(logger, hasSize(0))
            verify(featureFlagSetter).setFromAppInfo(data.apps.android)
        }

    @Test
    fun `available set to false - unavailable `() =
        runTest {
            logger = MemorisedLogger()
            setup()
            whenever(remoteSource.get()).thenReturn(AppInfoRemoteState.Success(dataAppUnavailable))
            whenever(localSource.get()).thenReturn(AppInfoLocalState.Success(dataAppUnavailable))
            val result = sut.get()
            assertEquals(AppInfoServiceState.Unavailable, result)
            assertThat(logger, hasSize(0))
            verify(featureFlagSetter).setFromAppInfo(dataAppUnavailable.apps.android)
        }
}
