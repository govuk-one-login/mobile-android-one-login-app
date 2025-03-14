package uk.gov.onelogin.features.settings.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.matcher.UriMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.features.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class OsslScreenTest : FragmentActivityTestCase() {
    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun osslScreenDisplayed() {
        val context: Context = ApplicationProvider.getApplicationContext()
        composeTestRule.apply {
            setContent {
                OsslScreen()
            }

            onNodeWithText(
                context.getString(R.string.app_osslTitle)
            ).assertIsDisplayed()
        }
    }

    @Test
    fun checkALinkOpensTheCorrectUrl() {
        val apacheUrl = Uri.parse("https://spdx.org/licenses/Apache-2.0.html")
        composeTestRule.setContent {
            OsslScreen()
        }

        composeTestRule.onAllNodes(hasText("Apache License 2.0"))[0].performClick()

        val host = apacheUrl.host
        val path = apacheUrl.path
        val scheme = apacheUrl.scheme
        intended(
            allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                hasData(UriMatchers.hasHost(host)),
                hasData(UriMatchers.hasPath(path)),
                hasData(UriMatchers.hasScheme(scheme))
            )
        )
    }
}
