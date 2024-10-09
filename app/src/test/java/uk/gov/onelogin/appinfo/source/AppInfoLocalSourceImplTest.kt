package uk.gov.onelogin.appinfo.source

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStoreErrorType
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.source.data.AppInfoLocalSourceImpl
import uk.gov.onelogin.appinfo.source.data.AppInfoLocalSourceImpl.Companion.APP_INFO_KEY
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoLocalState
import kotlin.test.assertEquals

class AppInfoLocalSourceImplTest {
    private val secureStore: SecureStore = mock()
    private val encodedValue = ClassLoader
        .getSystemResource("api/appInfoApiResponseValue.json").readText()
    private val data = AppInfoData(
        apps = AppInfoData.App(
            AppInfoData.AppInfo(
                minimumVersion = "0.0.0",
                releaseFlags = AppInfoData.ReleaseFlags(
                    true, true, true
                ),
                available = true,
                featureFlags = AppInfoData.FeatureFlags(true)
            )
        )
    )

    private val sut = AppInfoLocalSourceImpl(secureStore)

    @Test
    fun `successful retrieval`() = runTest {
        whenever(secureStore.retrieve(eq(APP_INFO_KEY))).thenReturn(RetrievalEvent.Success(encodedValue))
        val result = sut.get()
        assertEquals(AppInfoLocalState.Success(data), result)
    }

    @Test
    fun `failed retrieval`() = runTest {
        whenever(secureStore.retrieve(eq(APP_INFO_KEY)))
            .thenReturn(RetrievalEvent.Failed(SecureStoreErrorType.GENERAL))
        val result = sut.get()
        assertEquals(
            AppInfoLocalState.Failure(AppInfoLocalSourceImpl.APP_INFO_LOCAL_SOURCE_ERROR), result
        )
    }

    @Test
    fun `successful update`() = runTest {
        val result = sut.update(encodedValue)
        assertEquals(Unit, result)
    }
}
