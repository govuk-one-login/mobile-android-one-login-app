package uk.gov.onelogin.features.component.error.update

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.appinfo.AppInfoUtils
import uk.gov.onelogin.features.error.ui.update.ErrorUpdateRequiredScreen
import uk.gov.onelogin.features.error.ui.update.OutdatedAppErrorAnalyticsViewModel
import uk.gov.onelogin.features.error.ui.update.OutdatedAppErrorViewModel

@RunWith(AndroidJUnit4::class)
class ErrorUpdateRequiredErrorScreenTest : FragmentActivityTestCase() {
    private val intent = Intent()
    private val result = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
    private lateinit var primaryButton: SemanticsMatcher
    private lateinit var viewModel: OutdatedAppErrorViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: OutdatedAppErrorAnalyticsViewModel

    @Before
    fun setup() {
        viewModel = OutdatedAppErrorViewModel(context)
        analytics = mock()
        analyticsViewModel = OutdatedAppErrorAnalyticsViewModel(context, analytics)
        Intents.init()
        primaryButton = hasText(context.getString(R.string.app_updateAppButton))
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun verifyScreenDisplayed() {
        composeTestRule.setContent {
            ErrorUpdateRequiredScreen(
                viewModel = viewModel,
                analyticsViewModel = analyticsViewModel
            )
        }
        composeTestRule.apply {
            onNodeWithContentDescription(
                resources.getString(R.string.app_updateApp_ContentDescription)
            ).assertIsDisplayed()

            onNodeWithText(
                resources.getString(R.string.app_updateApp_Title)
            ).assertIsDisplayed()

            onNodeWithText(
                resources.getString(R.string.app_updateAppBody1)
            ).assertIsDisplayed()

            onNodeWithText(
                resources.getString(R.string.app_updateAppBody2)
            ).assertIsDisplayed()
        }
    }

    @Test
    fun verifyIntent() {
        composeTestRule.setContent {
            ErrorUpdateRequiredScreen(
                viewModel = viewModel,
                analyticsViewModel = analyticsViewModel
            )
        }
        Intents.intending(IntentMatchers.hasData(AppInfoUtils.Companion.GOOGLE_PLAY_URL)).respondWith(result)
        composeTestRule.onNode(primaryButton).performClick()
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_VIEW))
        Intents.intended(IntentMatchers.hasData(Uri.parse(AppInfoUtils.Companion.GOOGLE_PLAY_URL)))
    }

    @Test
    fun screenViewAnalyticsLogOnResume() {
        composeTestRule.setContent {
            ErrorUpdateRequiredScreen(
                viewModel = viewModel,
                analyticsViewModel = analyticsViewModel
            )
        }
        val event = OutdatedAppErrorAnalyticsViewModel.Companion.makeUpdateRequiredViewEvent(context)

        verify(analytics).logEventV3Dot1(event)
    }

    @Test
    fun updateAnalyticsLogOnUpdateAppButton() {
        composeTestRule.setContent {
            ErrorUpdateRequiredScreen(
                viewModel = viewModel,
                analyticsViewModel = analyticsViewModel
            )
        }
        val event = OutdatedAppErrorAnalyticsViewModel.Companion.makeUpdateEvent(context)
        Intents.intending(IntentMatchers.hasData(AppInfoUtils.Companion.GOOGLE_PLAY_URL)).respondWith(result)
        composeTestRule.onNode(primaryButton).performClick()
        verify(analytics).logEventV3Dot1(event)
    }

    @Test
    fun iconAnalyticsLogOnBackButton() {
        composeTestRule.setContent {
            ErrorUpdateRequiredScreen(
                viewModel = viewModel,
                analyticsViewModel = analyticsViewModel
            )
        }
        val event = OutdatedAppErrorAnalyticsViewModel.Companion.makeBackEvent(context)
        composeTestRule.waitForIdle()
        try {
            Espresso.pressBack()
        } catch (_: NoActivityResumedException) {
        }

        verify(analytics).logEventV3Dot1(event)
    }
}
