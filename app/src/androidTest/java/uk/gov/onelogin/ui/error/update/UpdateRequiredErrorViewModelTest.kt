package uk.gov.onelogin.ui.error.update

import android.content.Intent
import android.net.Uri
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.rule.IntentsTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.gov.onelogin.MainActivity
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.appinfo.AppInfoUtils

@HiltAndroidTest
class UpdateRequiredErrorViewModelTest : TestCase() {
    @get:Rule
    private val intentTestRule = IntentsTestRule(MainActivity::class.java)

    private val viewModel = UpdateRequiredErrorViewModel()
    private val intent = Intent()

    @Before
    fun setup() {
        hiltRule.inject()
        intentTestRule.launchActivity(intent)
    }

    @After
    fun tearoff() {
        Intents.release()
    }

    @Test
    fun updateApp() {
        composeTestRule.activityRule.scenario.onActivity {
            viewModel.updateApp(context)
        }

        intended(hasAction(Intent.ACTION_VIEW))
        intended(hasData(Uri.parse(AppInfoUtils.GOOGLE_PLAY_URL)))
    }
}
