package uk.gov.onelogin.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.features.FeatureFlags
import uk.gov.android.features.InMemoryFeatureFlags
import uk.gov.android.onelogin.R
import uk.gov.logging.api.analytics.extensions.getEnglishString
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel2
import uk.gov.logging.api.analytics.parameters.data.TaxonomyLevel3
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.logging.api.v3dot1.model.TrackEvent
import uk.gov.logging.api.v3dot1.model.ViewEvent
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

    @Before
    fun setup() {
        composeTestRule.setupComposeTestRule { _ ->
            HomeScreen()
        }
    }

    @Test
    fun homeScreenDisplayed() {
        composeTestRule.apply {
            onNodeWithText(
                resources.getString(R.string.app_homeTitle)
            ).assertIsDisplayed()

            onNodeWithTag(
                resources.getString(R.string.app_cri_card_test_tag),
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

        verify(mockAnalyticsLogger).logEventV3Dot1(screenEvent)
        verify(mockAnalyticsLogger).logEventV3Dot1(backButtonEvent)
        verify(mockAnalyticsLogger).logEventV3Dot1(cardLinkEvent)
    }

    private val screenEvent = ViewEvent.Screen(
        name = context.getEnglishString(R.string.app_home),
        id = context.getEnglishString(R.string.home_page_id),
        params = RequiredParameters(
            taxonomyLevel2 = TaxonomyLevel2.HOME,
            taxonomyLevel3 = TaxonomyLevel3.UNDEFINED
        )
    )

    private val cardLinkEvent = TrackEvent.Link(
        isExternal = true,
        domain = context.getEnglishString(R.string.app_oneLoginCardLinkUrl),
        text = context.getEnglishString(R.string.app_oneLoginCardLink),
        params = RequiredParameters(
            taxonomyLevel2 = TaxonomyLevel2.APP_SYSTEM,
            taxonomyLevel3 = TaxonomyLevel3.UNDEFINED
        )
    )

    private val backButtonEvent = TrackEvent.Icon(
        text = context.getEnglishString(R.string.system_backButton),
        params = RequiredParameters(
            taxonomyLevel2 = TaxonomyLevel2.APP_SYSTEM,
            taxonomyLevel3 = TaxonomyLevel3.UNDEFINED
        )
    )
}
