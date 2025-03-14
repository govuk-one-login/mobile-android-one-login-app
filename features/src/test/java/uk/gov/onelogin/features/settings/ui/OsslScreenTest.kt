package uk.gov.onelogin.features.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.espresso.Espresso
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.features.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class OsslScreenTest : FragmentActivityTestCase() {
    private lateinit var analyticsLogger: AnalyticsLogger
    private lateinit var analyticsViewModel: OsslAnalyticsViewModel

    @Before
    fun setup() {
        analyticsLogger = mock()
        analyticsViewModel = OsslAnalyticsViewModel(context, analyticsLogger)
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun osslScreenDisplayed() {
        composeTestRule.apply {
            setContent {
                OsslScreen(analyticsViewModel)
            }

            onNodeWithText(
                context.getString(R.string.app_osslTitle)
            ).assertIsDisplayed()
        }

        verify(analyticsLogger).logEventV3Dot1(OsslAnalyticsViewModel.makeScreenEvent(context))
    }

    @Test
    fun checkALinkOpensTheCorrectUrl() {
        val apacheUrl = Uri.parse("https://spdx.org/licenses/Apache-2.0.html")
        composeTestRule.setContent {
            OsslScreen(analyticsViewModel)
        }

        composeTestRule.apply {
            waitUntil { onAllNodes(hasText("Apache License 2.0", true))[0].isDisplayed() }
            onAllNodes(hasText("Apache License 2.0", true))[0]
                .performScrollTo()
                .performClick()
        }

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

    @Test
    fun backButtonAnalytics() {
        composeTestRule.setContent {
            OsslScreen(analyticsViewModel)
        }

        Espresso.pressBack()

        verify(analyticsLogger).logEventV3Dot1(
            OsslAnalyticsViewModel.makeBackButtonEvent(context)
        )
    }
}
