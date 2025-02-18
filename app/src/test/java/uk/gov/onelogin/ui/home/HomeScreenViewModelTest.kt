package uk.gov.onelogin.ui.home

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.extensions.CoroutinesTestExtension
import uk.gov.onelogin.extensions.InstantExecutorExtension
import uk.gov.onelogin.navigation.Navigator

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class HomeScreenViewModelTest {
    private val mockNavigator: Navigator = mock()
    private val httpClient: GenericHttpClient = mock()
    private val analyticsLogger: AnalyticsLogger = mock()
    private val featureFlag: FeatureFlags = mock()

    private val viewModel by lazy {
        HomeScreenViewModel(
            httpClient,
            analyticsLogger,
            featureFlag,
            mockNavigator
        )
    }

    @Test
    fun getUiCardFlagState() {
        whenever(featureFlag[any()]).thenReturn(true)
        assertTrue(viewModel.uiCardEnabled.value)

        whenever(featureFlag[any()]).thenReturn(false)
        viewModel.getUiCardFlagState()
        assertFalse(viewModel.uiCardEnabled.value)
    }

    @Test
    fun openDevPanel() {
        viewModel.openDevPanel()

        verify(mockNavigator).openDeveloperPanel()
    }
}
