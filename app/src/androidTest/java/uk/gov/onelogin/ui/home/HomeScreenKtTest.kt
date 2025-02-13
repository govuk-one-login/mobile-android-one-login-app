package uk.gov.onelogin.ui.home

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.features.FeatureFlags
import uk.gov.android.features.InMemoryFeatureFlags
import uk.gov.android.onelogin.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.core.analytics.AnalyticsModule
import uk.gov.onelogin.ext.setupComposeTestRule
import uk.gov.onelogin.features.CriCardFeatureFlag
import uk.gov.onelogin.features.FeaturesModule
import uk.gov.onelogin.features.WalletFeatureFlag

@Suppress("ForbiddenComment")
@HiltAndroidTest
@UninstallModules(AnalyticsModule::class, FeaturesModule::class)
class HomeScreenKtTest : TestCase() {
    @BindValue
    var analytics: FirebaseAnalytics = Firebase.analytics

    @BindValue
    var mockAnalyticsLogger: AnalyticsLogger = mock()

    // TODO: Remove this after `activeSession` has been added to the CriOrchestrator and test using the stub
    //  provided
    @BindValue
    var featureFlags: FeatureFlags = InMemoryFeatureFlags(
        setOf(WalletFeatureFlag.ENABLED, CriCardFeatureFlag.ENABLED)
    )

    private val intent = Intent()
    private val result = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

    @Before
    fun setup() {
        Intents.init()
        composeTestRule.setupComposeTestRule { _ ->
            HomeScreen()
        }
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun homeScreenDisplayed() {
        composeTestRule.apply {
            onNodeWithText(
                resources.getString(R.string.app_homeTitle)
            ).assertIsDisplayed()

            onNodeWithTag(
                resources.getString(R.string.appCriCardTestTag),
                useUnmergedTree = true
            ).assertIsDisplayed()

            onNodeWithTag(
                resources.getString(R.string.yourServicesCardTestTag),
                useUnmergedTree = true
            ).assertIsDisplayed()

            onNodeWithText("Developer Panel").assertIsDisplayed()
        }
    }

    @Test
    fun analyticsTriggered() {
        // This will stop the test from throwing an error due to missing intent on pipeline
        intending(hasData(context.getString(R.string.app_oneLoginCardLinkUrl))).respondWith(result)
        composeTestRule.apply {
            activityRule.scenario.onActivity { activity ->
                activity.onBackPressedDispatcher.onBackPressed()
            }

            onNodeWithText(
                resources.getString(R.string.app_oneLoginCardLink),
                useUnmergedTree = true,
                substring = true
            ).performClick()
        }

        verify(mockAnalyticsLogger).logEventV3Dot1(
            HomeScreenAnalyticsViewModel.makeScreenEvent(context)
        )
        verify(mockAnalyticsLogger).logEventV3Dot1(
            HomeScreenAnalyticsViewModel.makeBackButtonEvent(context)
        )
        verify(mockAnalyticsLogger).logEventV3Dot1(
            HomeScreenAnalyticsViewModel.makeCardLinkEvent(context)
        )
    }
}
