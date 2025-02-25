package uk.gov.onelogin.features.error.ui.update

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
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.features.TestCase
import uk.gov.onelogin.features.appinfo.AppInfoUtils

class UpdateRequiredErrorScreenTest : TestCase() {
    private val intent = Intent()
    private val result = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
    private lateinit var primaryButton: SemanticsMatcher
    private lateinit var viewModel: UpdateRequiredErrorViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: UpdateRequiredAnalyticsViewModel

    @Before
    fun setup() {
        viewModel = UpdateRequiredErrorViewModel(context)
        analytics = mock()
        analyticsViewModel = UpdateRequiredAnalyticsViewModel(context, analytics)
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
            UpdateRequiredScreen(viewModel = viewModel, analyticsViewModel = analyticsViewModel)
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
            UpdateRequiredScreen(viewModel = viewModel, analyticsViewModel = analyticsViewModel)
        }
        intending(hasData(AppInfoUtils.GOOGLE_PLAY_URL)).respondWith(result)
        composeTestRule.onNode(primaryButton).performClick()
        intended(hasAction(Intent.ACTION_VIEW))
        intended(hasData(Uri.parse(AppInfoUtils.GOOGLE_PLAY_URL)))
    }

    @Test
    fun screenViewAnalyticsLogOnResume() {
        composeTestRule.setContent {
            UpdateRequiredScreen(viewModel = viewModel, analyticsViewModel = analyticsViewModel)
        }
        val event = UpdateRequiredAnalyticsViewModel.makeUpdateRequiredViewEvent(context)

        verify(analytics).logEventV3Dot1(event)
    }

    @Test
    fun updateAnalyticsLogOnUpdateAppButton() {
        composeTestRule.setContent {
            UpdateRequiredScreen(viewModel = viewModel, analyticsViewModel = analyticsViewModel)
        }
        val event = UpdateRequiredAnalyticsViewModel.makeUpdateEvent(context)
        intending(hasData(AppInfoUtils.GOOGLE_PLAY_URL)).respondWith(result)
        composeTestRule.onNode(primaryButton).performClick()
        verify(analytics).logEventV3Dot1(event)
    }

    @Test
    fun iconAnalyticsLogOnBackButton() {
        composeTestRule.setContent {
            UpdateRequiredScreen(viewModel = viewModel, analyticsViewModel = analyticsViewModel)
        }
        val event = UpdateRequiredAnalyticsViewModel.makeBackEvent(context)
        composeTestRule.waitForIdle()
        try {
            Espresso.pressBack()
        } catch (_: NoActivityResumedException) {
        }

        verify(analytics).logEventV3Dot1(event)
    }
}
