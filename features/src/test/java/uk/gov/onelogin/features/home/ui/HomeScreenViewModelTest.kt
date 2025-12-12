package uk.gov.onelogin.features.home.ui

import android.content.Context
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.logging.api.Logger
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.criorchestrator.sdk.publicapi.CriOrchestratorSdkExt.create
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk
import uk.gov.onelogin.features.TestUtils
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.extensions.InstantExecutorExtension
import uk.gov.onelogin.features.wallet.data.WalletRepository

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class HomeScreenViewModelTest {
    private val mockNavigator: Navigator = mock()
    private val httpClient: GenericHttpClient = mock()
    private val analyticsLogger: AnalyticsLogger = mock()
    private val featureFlag: FeatureFlags = mock()
    private val context: Context = mock()
    private val logger: Logger = mock()
    private val criOrchestratorSdk: CriOrchestratorSdk = CriOrchestratorSdk.create(
        authenticatedHttpClient = httpClient,
        analyticsLogger = analyticsLogger,
        initialConfig = TestUtils.criSdkConfig,
        logger = logger,
        applicationContext = context
    )

    private val walletRepository: WalletRepository = mock()

    private val viewModel by lazy {
        HomeScreenViewModel(
            featureFlag,
            mockNavigator,
            walletRepository,
            criOrchestratorSdk
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

    @Test
    fun `no wallet deeplink`() {
        // WHEN
        whenever(walletRepository.isWalletDeepLinkPath()).thenReturn(false)
        viewModel.checkWalletEnabled()

        // THEN
        // Times 2 because once when is initialised, and once when called specifically - this test
        // all possible use cases when coming in via deeplink and when returning back to the tab from any others
        verify(walletRepository, times(0)).setWalletDeepLinkPathState(any())
    }

    @Test
    fun `received wallet deeplink`() {
        // WHEN
        whenever(walletRepository.isWalletDeepLinkPath()).thenReturn(true)
        viewModel.checkWalletEnabled()

        // THEN
        // Times 2 because once when is initialised, and once when called specifically - this test
        // all possible use cases when coming in via deeplink and when returning back to the tab from any others
        verify(walletRepository, times(1)).setWalletDeepLinkPathState(deepLink = false)
    }
}
