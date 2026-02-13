package uk.gov.onelogin.features.settings.ui.ossl

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
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
import uk.gov.logging.api.Logger
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.features.FragmentActivityTestCase
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class OsslScreenTest : FragmentActivityTestCase() {
    private lateinit var analyticsLogger: AnalyticsLogger
    private lateinit var analyticsViewModel: OsslAnalyticsViewModel
    private lateinit var crashLogger: Logger

    @Before
    fun setup() {
        analyticsLogger = mock()
        crashLogger = mock()
        analyticsViewModel = OsslAnalyticsViewModel(context, analyticsLogger, crashLogger)
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
        var backClicked = false

        composeTestRule.setContent {
            OsslScreen(analyticsViewModel) {
                backClicked = true
            }
        }

        Espresso.pressBack()

        verify(analyticsLogger).logEventV3Dot1(
            OsslAnalyticsViewModel.makeBackButtonEvent(context)
        )
        assertTrue(backClicked)
    }

    @Test
    fun backIconAnalytics() {
        var backClicked = false

        composeTestRule.setContent {
            OsslScreen(analyticsViewModel) {
                backClicked = true
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                resources.getString(R.string.app_back_icon)
            ).performClick()

        verify(analyticsLogger).logEventV3Dot1(
            OsslAnalyticsViewModel.makeBackIconEvent(context)
        )
        assertTrue(backClicked)
    }
}
