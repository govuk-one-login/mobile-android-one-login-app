package uk.gov.onelogin.features.component.navigation.domain

import android.content.Intent
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.times
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.matcher.UriMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.logging.api.v3.LogLevel
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.isLogLevel
import uk.gov.onelogin.core.utils.ActivityProviderImpl
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.navigation.domain.WebNavigatorImpl

@RunWith(AndroidJUnit4::class)
class WebNavigatorImplTest : FragmentActivityTestCase() {
    private val activityProvider = ActivityProviderImpl()
    private val logger = MemorisedLogger()
    private val webNavigator = WebNavigatorImpl(activityProvider, logger)

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun `given activity is available, when openWebBrowser is called, it fires ACTION_VIEW intent with correct uri`() {
        activityProvider.setCurrentActivity(composeTestRule.activity)

        webNavigator.openWebBrowser("https://example.com")

        intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(UriMatchers.hasHost("example.com")),
                hasData(UriMatchers.hasScheme("https")),
            ),
        )
    }

    @Test
    fun `given activity is null, when openWebBrowser is called, it does not fire any intent`() {
        webNavigator.openWebBrowser("https://example.com")

        intended(hasAction(Intent.ACTION_VIEW), times(0))
    }

    @Test
    fun `given activity is null, when openWebBrowser is called, it logs an error`() {
        webNavigator.openWebBrowser("https://example.com")

        assertThat(
            logger,
            hasItem(
                allOf(isLogLevel(LogLevel.Error), hasMessage("Navigation to web failed")),
            ),
        )
    }
}
