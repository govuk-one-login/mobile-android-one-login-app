package uk.gov.onelogin.optin.data

import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import uk.gov.onelogin.optin.domain.model.AnalyticsOptInState
import uk.gov.onelogin.optin.domain.source.OptInRemoteSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@ExperimentalCoroutinesApi
class FirebaseAnalyticsOptInSourceTest {
    private val dispatcher = StandardTestDispatcher()
    private val analytics: FirebaseAnalytics = mock()
    private lateinit var source: OptInRemoteSource

    @BeforeTest
    fun setUp() {
        source = FirebaseAnalyticsOptInSource(analytics, dispatcher)
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `update does nothing for None`() = runTest {
        // Given OptInRemoteSource
        // When calling update with None
        source.update(AnalyticsOptInState.None)
        // Then nothing happens
        verifyNoInteractions(analytics)
    }

    @Test
    fun `update with No turns collection off`() = runTest {
        // Given OptInRemoteSource
        // When calling update with No
        source.update(AnalyticsOptInState.No)
        // Then FirebaseAnalytics has analytics collection turned off
        verify(analytics).setAnalyticsCollectionEnabled(false)
    }

    @Test
    fun `update with Yes turns collection on`() = runTest {
        // Given OptInRemoteSource
        // When calling update with Yes
        source.update(AnalyticsOptInState.Yes)
        // Then FirebaseAnalytics has analytics collection turned on
        verify(analytics).setAnalyticsCollectionEnabled(true)
    }
}
