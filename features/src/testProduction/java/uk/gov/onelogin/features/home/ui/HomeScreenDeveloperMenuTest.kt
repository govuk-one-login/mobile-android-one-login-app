package uk.gov.onelogin.features.home.ui

import androidx.compose.ui.test.onNodeWithText
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.featureflags.InMemoryFeatureFlags
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.logging.api.Logger
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.criorchestrator.sdk.publicapi.CriOrchestratorSdkExt.create
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.TestUtils
import uk.gov.onelogin.features.ext.setupComposeTestRule
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.wallet.data.WalletRepository

@RunWith(AndroidJUnit4::class)
@Suppress("ForbiddenComment")
class HomeScreenDeveloperMenuTest : FragmentActivityTestCase() {
    private lateinit var httpClient: GenericHttpClient
    private lateinit var analyticsLogger: AnalyticsLogger
    private lateinit var logger: Logger
    private lateinit var criOrchestratorSdk: CriOrchestratorSdk

    // TODO: Remove this after `activeSession` has been added to the CriOrchestrator and test using the stub
    //  provided
    private lateinit var featureFlags: FeatureFlags
    private lateinit var navigator: Navigator
    private lateinit var viewModel: HomeScreenViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: HomeScreenAnalyticsViewModel
    private lateinit var walletRepository: WalletRepository

    @Before
    fun setup() {
        Intents.init()
        httpClient = mock()
        analyticsLogger = mock()
        featureFlags =
            InMemoryFeatureFlags(
                setOf(WalletFeatureFlag.ENABLED)
            )
        navigator = mock()
        logger = mock()
        criOrchestratorSdk = CriOrchestratorSdk.create(
            authenticatedHttpClient = httpClient,
            analyticsLogger = analyticsLogger,
            initialConfig = TestUtils.criSdkConfig,
            logger = logger,
            applicationContext = context
        )
        walletRepository = mock()
        viewModel = HomeScreenViewModel(
            featureFlags,
            navigator,
            walletRepository,
            criOrchestratorSdk
        )
        analytics = mock()
        analyticsViewModel = HomeScreenAnalyticsViewModel(context, analytics)
        composeTestRule.setupComposeTestRule { _ ->
            HomeScreen(viewModel, analyticsViewModel)
        }
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun homeScreenDisplayed() {
        composeTestRule.onNodeWithText("Developer Panel", useUnmergedTree = true)
            .assertDoesNotExist()
    }
}
