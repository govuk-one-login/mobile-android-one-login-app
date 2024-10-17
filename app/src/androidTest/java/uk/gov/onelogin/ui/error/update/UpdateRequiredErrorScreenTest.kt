package uk.gov.onelogin.ui.error.update

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.appinfo.AppInfoUtils

@HiltAndroidTest
class UpdateRequiredErrorScreenTest : TestCase() {
    private val intent = Intent()
    private val result = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

    @Before
    fun setup() {
        hiltRule.inject()
        Intents.init()
        composeTestRule.setContent {
            UpdateRequiredScreen()
        }
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun verifyScreenDisplayed() {
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
        intending(hasData(AppInfoUtils.GOOGLE_PLAY_URL)).respondWith(result)
        composeTestRule.apply {
            onNodeWithText(resources.getString(R.string.app_updateAppButton)).performClick()
        }

        intended(hasAction(Intent.ACTION_VIEW))
        intended(hasData(Uri.parse(AppInfoUtils.GOOGLE_PLAY_URL)))
    }
}
