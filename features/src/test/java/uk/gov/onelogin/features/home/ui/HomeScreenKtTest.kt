package uk.gov.onelogin.features.home.ui

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.featureflags.InMemoryFeatureFlags
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.Logger
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.criorchestrator.sdk.publicapi.CriOrchestratorSdkExt.create
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.TestUtils
import uk.gov.onelogin.features.ext.setupComposeTestRule
import uk.gov.onelogin.features.featureflags.data.CriOrchestratorFeatureFlag
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

@RunWith(AndroidJUnit4::class)
@Suppress("ForbiddenComment")
class HomeScreenKtTest : FragmentActivityTestCase() {
    private lateinit var httpClient: GenericHttpClient
    private lateinit var analyticsLogger: AnalyticsLogger
    private lateinit var criOrchestratorSdk: CriOrchestratorSdk

    // TODO: Remove this after `activeSession` has been added to the CriOrchestrator and test using the stub
    //  provided
    private lateinit var featureFlags: FeatureFlags
    private lateinit var navigator: Navigator
    private lateinit var logger: Logger
    private lateinit var viewModel: HomeScreenViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: HomeScreenAnalyticsViewModel

    private val intent = Intent()
    private val result = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

    @Before
    fun setup() {
        Intents.init()
        httpClient = mock()
        analyticsLogger = mock()
        featureFlags =
            InMemoryFeatureFlags(
                setOf(WalletFeatureFlag.ENABLED, CriOrchestratorFeatureFlag.ENABLED)
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
        viewModel = HomeScreenViewModel(
            featureFlags,
            navigator,
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
        composeTestRule.apply {
            waitUntil(TIMEOUT) {
                onNodeWithContentDescription(
                    "Close",
                    substring = true,
                    useUnmergedTree = true
                ).isDisplayed()
            }

            onNodeWithContentDescription(
                "Close",
                substring = true,
                useUnmergedTree = true
            ).performClick()

            onNodeWithText(
                resources.getString(R.string.app_homeTitle)
            ).assertIsDisplayed()

            onNodeWithTag(
                resources.getString(R.string.appCriCardTestTag),
                useUnmergedTree = true
            ).assertIsDisplayed()

            onNodeWithText(
                "Continue proving your identity",
                useUnmergedTree = true
            ).performClick()

            waitUntil(TIMEOUT) {
                onNodeWithContentDescription(
                    "Close",
                    substring = true,
                    useUnmergedTree = true
                ).isDisplayed()
            }

            onNodeWithContentDescription(
                "Close",
                substring = true,
                useUnmergedTree = true
            ).performClick()

            onNodeWithTag(
                resources.getString(R.string.yourServicesCardTestTag),
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }

    @Test
    fun analyticsTriggered() {
        // This will stop the test from throwing an error due to missing intent on pipeline
        intending(hasData(context.getString(R.string.app_oneLoginCardLinkUrl))).respondWith(result)
        composeTestRule.apply {
            waitUntil(TIMEOUT) {
                onNodeWithContentDescription(
                    "Close",
                    substring = true,
                    useUnmergedTree = true
                ).isDisplayed()
            }

            onNodeWithContentDescription(
                "Close",
                substring = true,
                useUnmergedTree = true
            ).performClick()

            Espresso.pressBack()

            onNodeWithText(
                resources.getString(R.string.app_oneLoginCardLink),
                useUnmergedTree = true,
                substring = true
            ).performClick()
        }
    }

    companion object {
        private const val TIMEOUT = 10000L
    }
}
