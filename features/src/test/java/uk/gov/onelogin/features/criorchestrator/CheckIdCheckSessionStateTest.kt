package uk.gov.onelogin.features.criorchestrator

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertFalse
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.logging.api.Logger
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.criorchestrator.features.config.publicapi.Config
import uk.gov.onelogin.criorchestrator.features.idcheckwrapper.publicapi.idchecksdkactivestate.IsIdCheckSdkActive
import uk.gov.onelogin.criorchestrator.features.idcheckwrapper.publicapi.idchecksdkactivestate.isIdCheckSdkActive
import uk.gov.onelogin.criorchestrator.sdk.publicapi.CriOrchestratorSdkExt.create
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk
import uk.gov.onelogin.features.FragmentActivityTestCase
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class CheckIdCheckSessionStateTest : FragmentActivityTestCase() {
    private val mockHttpClient: GenericHttpClient = mock()
    private val mockAnalyticsLogger: AnalyticsLogger = mock()
    private val mockConfig: Config = mock()
    private val mockLogger: Logger = mock()
    private val mockCriOrchestratorSdk: CriOrchestratorSdk =
        CriOrchestratorSdk.create(
            authenticatedHttpClient = mockHttpClient,
            analyticsLogger = mockAnalyticsLogger,
            initialConfig = mockConfig,
            logger = mockLogger,
            applicationContext = context
        )
    private val c = mock<java.util.Collection<String>>()

    private lateinit var mockCheckIdCheckSessionState: CheckIdCheckSessionState

    @Before
    fun setup() {
        whenever(c.toArray()).thenReturn(arrayOf("c"))
        mockCheckIdCheckSessionState = CheckIdCheckSessionStateImpl(mockCriOrchestratorSdk)
    }

    @Ignore(
        "To find a way ot test this - at the moment is because of this error: " +
            "Cannot invoke \"java.util.Collection.toArray()\" because \"c\" is null"
    )
    @Test
    fun sessionIsActive() {
        whenever(mockCriOrchestratorSdk.isIdCheckSdkActive).thenReturn(
            MockIsIdSCheckSessionActiveImpl(true)
        )

        val result = mockCheckIdCheckSessionState.isIdCheckActive()

        assertTrue(result)
    }

    @Ignore(
        "To find a way ot test this - at the moment is because of this error: " +
            "Cannot invoke \"java.util.Collection.toArray()\" because \"c\" is null"
    )
    @Test
    fun sessionIsNotActive() {
        whenever(mockCriOrchestratorSdk.isIdCheckSdkActive).thenReturn(
            MockIsIdSCheckSessionActiveImpl(false)
        )

        val result = mockCheckIdCheckSessionState.isIdCheckActive()

        assertFalse(result)
    }

    private class MockIsIdSCheckSessionActiveImpl(
        private val result: Boolean
    ) : IsIdCheckSdkActive {
        override fun invoke(): Boolean = result
    }
}
