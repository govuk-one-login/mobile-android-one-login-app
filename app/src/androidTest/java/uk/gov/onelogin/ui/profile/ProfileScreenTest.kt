package uk.gov.onelogin.ui.profile

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.matcher.IntentMatchers.isInternal
import androidx.test.espresso.intent.matcher.UriMatchers
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.onelogin.R
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.navigation.NavigatorModule
import uk.gov.onelogin.optin.ui.NOTICE_TAG
import uk.gov.onelogin.signOut.SignOutRoutes

@HiltAndroidTest
@UninstallModules(NavigatorModule::class)
class ProfileScreenTest : TestCase() {
    @BindValue
    val mockNavigator: Navigator = mock()

    private lateinit var yourDetailsHeader: SemanticsMatcher
    private lateinit var yourDetailsTitle: SemanticsMatcher
    private lateinit var yourDetailsSubTitle: SemanticsMatcher
    private lateinit var legalHeader: SemanticsMatcher
    private lateinit var legalLink1: SemanticsMatcher
    private lateinit var legalLink2: SemanticsMatcher
    private lateinit var aboutTheAppSwitch: SemanticsMatcher
    private lateinit var aboutTheAppSubTitle: SemanticsMatcher
    private lateinit var aboutTheAppPrivacyLink: SemanticsMatcher
    private lateinit var signOutButton: SemanticsMatcher

    @Before
    fun setUp() {
        yourDetailsHeader = hasText(resources.getString(R.string.app_profileSubtitle1))
        yourDetailsTitle = hasText(resources.getString(R.string.app_signInDetails))
        yourDetailsSubTitle = hasText(resources.getString(R.string.app_manageSignInDetailsFootnote))
        legalHeader = hasText(resources.getString(R.string.app_profileSubtitle2))
        legalLink1 = hasText(resources.getString(R.string.app_privacyNoticeLink2))
        legalLink2 = hasText(resources.getString(R.string.app_OpenSourceLicences))
        aboutTheAppSwitch = hasTestTag(resources.getString(R.string.optInSwitchTestTag))
        aboutTheAppSubTitle = hasText(
            resources.getString(R.string.app_settingsAnalyticsToggleFootnote),
            substring = true
        )
        aboutTheAppPrivacyLink = hasTestTag(NOTICE_TAG)
        signOutButton = hasText(resources.getString(R.string.app_signOutButton))
        Intents.init()
        intending(not(isInternal())).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        )
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun yourDetailsGroupDisplayed() {
        composeTestRule.setContent {
            ProfileScreen()
        }
        composeTestRule.onNode(yourDetailsHeader).assertIsDisplayed()
        composeTestRule.onNode(yourDetailsTitle).assertIsDisplayed()
        composeTestRule.onNode(yourDetailsSubTitle).assertIsDisplayed()
    }

    @Test
    fun legalGroupDisplayed() {
        composeTestRule.setContent {
            ProfileScreen()
        }
        composeTestRule.onNode(legalHeader).assertIsDisplayed()
        composeTestRule.onNode(legalLink1).assertIsDisplayed()
        composeTestRule.onNode(legalLink2).assertIsDisplayed()
    }

    @Test
    fun aboutTheAppSectionDisplayed() {
        composeTestRule.setContent {
            ProfileScreen()
        }
        composeTestRule.onNode(aboutTheAppSwitch).performScrollTo().assertIsDisplayed()
        composeTestRule.onNode(aboutTheAppPrivacyLink).performScrollTo().assertIsDisplayed()
        composeTestRule.onNode(aboutTheAppSubTitle).assertIsDisplayed()
    }

    @Test
    fun toggleSwitchCallOnToggleClickEvent() {
        var optInState = false
        composeTestRule.setContent {
            PreferenceToggleRow(
                title = R.string.app_settingsAnalyticsToggle,
                checked = optInState,
                onToggle = { optInState = true }
            )
        }
        composeTestRule.onNode(aboutTheAppSwitch).performClick()
        assert(optInState)
    }

    @Test
    fun privacyNoticeInAboutTheAppSectionLaunchesBrowser() {
        var optInState = false
        val privacyNoticeUrl = resources.getString(R.string.privacy_notice_url)
        val uriHandler = mock<UriHandler>()
        composeTestRule.setContent {
            AboutTheAppSection(optInState, uriHandler, privacyNoticeUrl) {
                optInState = true
            }
        }
        composeTestRule.onNode(aboutTheAppPrivacyLink, useUnmergedTree = true)
            .performTouchInput {
                click(bottomRight.copy(x = bottomRight.x - 10f))
            }
        verify(uriHandler).openUri(privacyNoticeUrl)
    }

    @Test
    fun signOutCta() {
        composeTestRule.setContent {
            ProfileScreen()
        }
        composeTestRule.onNode(signOutButton).performScrollTo().performClick()
        verify(mockNavigator).navigate(SignOutRoutes.Start)
    }

    @Test
    fun signInLaunchesBrowser() {
        // Given the ProfileScreen Composable
        val url = resources.getString(R.string.sign_in_url)
        val signInURL = Uri.parse(url)
        composeTestRule.setContent {
            ProfileScreen()
        }
        // When clicking on the sign in link
        composeTestRule.onNode(yourDetailsTitle).performClick()
        // Then open a browser with the correct URL
        val host = signInURL.host
        val path = signInURL.path
        val scheme = signInURL.scheme
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
    fun privacyNoticeLaunchesBrowser() {
        // Given the ProfileScreen Composable
        val url = resources.getString(R.string.privacy_notice_url)
        val signInURL = Uri.parse(url)
        composeTestRule.setContent {
            ProfileScreen()
        }
        // When clicking on the privacy notice link

        composeTestRule.onNode(legalLink1).performClick()
        // Then open a browser with the correct URL
        val host = signInURL.host
        val path = signInURL.path
        val scheme = signInURL.scheme
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
