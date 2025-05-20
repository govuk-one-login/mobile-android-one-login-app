package uk.gov.onelogin.features.settings.ui

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
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
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.core.navigation.data.SettingsRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.retrieve.GetEmail
import uk.gov.onelogin.core.ui.components.DIVIDER_TEST_TAG
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.optin.data.OptInRepository
import uk.gov.onelogin.features.optin.ui.NOTICE_TAG
import uk.gov.onelogin.features.settings.domain.BiometricsOptInChecker

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest : FragmentActivityTestCase() {
    private val optInRepository: OptInRepository = mock()
    private lateinit var navigator: Navigator
    private lateinit var getEmail: GetEmail
    private lateinit var biometricsOptInChecker: BiometricsOptInChecker
    private lateinit var tokenRepository: TokenRepository
    private lateinit var viewModel: SettingsScreenViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: SettingsAnalyticsViewModel

    private lateinit var yourDetailsHeader: SemanticsMatcher
    private lateinit var yourDetailsTitle: SemanticsMatcher
    private lateinit var yourDetailsSubTitle: SemanticsMatcher
    private lateinit var legalLink1: SemanticsMatcher
    private lateinit var legalLink2: SemanticsMatcher
    private lateinit var legalLink3: SemanticsMatcher
    private lateinit var helpLink: SemanticsMatcher
    private lateinit var contactLink: SemanticsMatcher
    private lateinit var aboutTheAppSwitch: SemanticsMatcher
    private lateinit var aboutTheAppSubTitle: SemanticsMatcher
    private lateinit var aboutTheAppPrivacyLink: SemanticsMatcher
    private lateinit var signOutButton: SemanticsMatcher
    private lateinit var openSourceLicensesButton: SemanticsMatcher
    private lateinit var biometricsOptIn: SemanticsMatcher

    @Before
    fun setUp() {
        whenever(optInRepository.isOptInPreferenceRequired())
            .thenReturn(MutableStateFlow(false))
        whenever(optInRepository.hasAnalyticsOptIn()).thenReturn(MutableStateFlow(false))
        navigator = mock()
        getEmail = mock()
        tokenRepository = mock()
        biometricsOptInChecker = mock()
        viewModel = SettingsScreenViewModel(
            optInRepository,
            navigator,
            biometricsOptInChecker,
            tokenRepository,
            getEmail
        )
        analytics = mock()
        analyticsViewModel = SettingsAnalyticsViewModel(context, analytics)
        yourDetailsHeader = hasText(resources.getString(R.string.app_settingsSubtitle1))
        yourDetailsTitle = hasText(resources.getString(R.string.app_settingsSignInDetailsLink))
        yourDetailsSubTitle = hasText(
            resources.getString(R.string.app_settingSignInDetailsFootnote)
        )
        legalLink1 = hasText(resources.getString(R.string.app_privacyNoticeLink2))
        legalLink2 = hasText(resources.getString(R.string.app_openSourceLicences))
        legalLink3 = hasText(resources.getString(R.string.app_accessibilityStatement))
        helpLink = hasText(resources.getString(R.string.app_appGuidanceLink))
        contactLink = hasText(resources.getString(R.string.app_contactLink))
        aboutTheAppSwitch = hasTestTag(resources.getString(R.string.optInSwitchTestTag))
        aboutTheAppSubTitle = hasText(
            resources.getString(R.string.app_settingsAnalyticsToggleFootnote),
            substring = true
        )
        aboutTheAppPrivacyLink = hasTestTag(NOTICE_TAG)
        signOutButton = hasText(resources.getString(R.string.app_signOutButton))
        openSourceLicensesButton = hasText(resources.getString(R.string.app_openSourceLicences))
        biometricsOptIn = hasText(context.getString(R.string.app_settingsBiometricsField))
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
    fun yourDetailsGroupDisplayed() = runTest {
        whenever(biometricsOptInChecker.getBiometricsOptInState()).thenReturn(flowOf(true))

        composeTestRule.setContent {
            SettingsScreen(viewModel, analyticsViewModel)
        }
        composeTestRule.onNodeWithTag(DIVIDER_TEST_TAG).assertIsDisplayed()
        composeTestRule.onNode(yourDetailsHeader).assertIsDisplayed()
        composeTestRule.onNode(yourDetailsTitle).assertIsDisplayed()
        composeTestRule.onNode(yourDetailsSubTitle).assertIsDisplayed()
        verify(analytics).logEventV3Dot1(SettingsAnalyticsViewModel.makeSettingsViewEvent(context))
    }

    @Test
    fun legalGroupDisplayed() = runTest {
        whenever(biometricsOptInChecker.getBiometricsOptInState()).thenReturn(flowOf(true))

        composeTestRule.setContent {
            SettingsScreen(viewModel, analyticsViewModel)
        }
        composeTestRule.onNode(legalLink1).performScrollTo().assertIsDisplayed()
        composeTestRule.onNode(legalLink2).performScrollTo().assertIsDisplayed()
        composeTestRule.onNode(legalLink3).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun aboutTheAppSectionDisplayed() = runTest {
        whenever(biometricsOptInChecker.getBiometricsOptInState()).thenReturn(flowOf(true))

        composeTestRule.setContent {
            SettingsScreen(viewModel, analyticsViewModel)
        }
        composeTestRule.apply {
            onNode(aboutTheAppSwitch, useUnmergedTree = true)
                .performScrollTo()
                .assertIsDisplayed()
            onNode(aboutTheAppPrivacyLink, useUnmergedTree = true)
                .performScrollTo()
                .assertIsDisplayed()
            onNode(aboutTheAppSubTitle, useUnmergedTree = true)
                .assertIsDisplayed()
        }
    }

    @Test
    fun toggleSwitchCallOnToggleClickEvent() = runTest {
        whenever(biometricsOptInChecker.getBiometricsOptInState()).thenReturn(flowOf(true))

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
    fun privacyNoticeInAboutTheAppSectionLaunchesBrowser() = runTest {
        whenever(biometricsOptInChecker.getBiometricsOptInState()).thenReturn(flowOf(true))

        var optInState = false
        var biometricsOptInState = false
        var biometricsClick = 0
        var privacyNoticeClicked = false

        composeTestRule.setContent {
            AboutTheAppSection(
                optInState,
                biometricsOptInState,
                onBiometrics = { biometricsClick++ },
                onToggle = { optInState = true },
                onPrivacyNoticeClick = { privacyNoticeClicked = true }
            )
        }
        composeTestRule.onNode(aboutTheAppPrivacyLink, useUnmergedTree = true)
            .performTouchInput {
                click(bottomRight.copy(x = bottomRight.x - 10f))
            }
        assertTrue(privacyNoticeClicked)
    }

    @Test
    fun biometricsDisplayedAndOnClickWorks() = runTest {
        whenever(biometricsOptInChecker.getBiometricsOptInState()).thenReturn(flowOf(true))

        composeTestRule.setContent {
            SettingsScreen(viewModel, analyticsViewModel)
        }
        composeTestRule.apply {
            onNode(biometricsOptIn, useUnmergedTree = true)
                .performScrollTo()
                .assertIsDisplayed()
                .performClick()
        }

        verify(navigator).navigate(SettingsRoutes.BiometricsOptIn)
    }

    @Test
    fun biometricsNotDisplayed() = runTest {
        whenever(biometricsOptInChecker.getBiometricsOptInState()).thenReturn(flowOf(false))

        composeTestRule.setContent {
            SettingsScreen(viewModel, analyticsViewModel)
        }
        composeTestRule.apply {
            onNode(biometricsOptIn, useUnmergedTree = true).assertIsNotDisplayed()
        }

        // This can be removed once functionality has been added to the ticket and navigation can be tested
        // Purpose - test click on Biometrics
        verify(navigator, times(0)).navigate(SettingsRoutes.BiometricsOptIn)
    }

    @Test
    fun signOutCta() = runTest {
        whenever(biometricsOptInChecker.getBiometricsOptInState()).thenReturn(flowOf(true))

        composeTestRule.setContent {
            SettingsScreen(viewModel, analyticsViewModel)
        }
        composeTestRule.onNode(signOutButton).performScrollTo().performClick()
        verify(navigator).navigate(SignOutRoutes.Start)
        verify(analytics).logEventV3Dot1(SettingsAnalyticsViewModel.makeSignOutEvent(context))
    }

    @Test
    fun openSourceLicensesCta() = runTest {
        whenever(biometricsOptInChecker.getBiometricsOptInState()).thenReturn(flowOf(true))

        composeTestRule.setContent {
            SettingsScreen(viewModel, analyticsViewModel)
        }
        composeTestRule.onNode(openSourceLicensesButton).performScrollTo().performClick()
        verify(navigator).navigate(SettingsRoutes.Ossl)
        verify(analytics).logEventV3Dot1(SettingsAnalyticsViewModel.makeOpenSourceEvent(context))
    }

    @Test
    fun signInLaunchesBrowser() = runTest {
        whenever(biometricsOptInChecker.getBiometricsOptInState()).thenReturn(flowOf(true))

        // Given the SettingsScreen Composable
        val url = resources.getString(R.string.app_manageSignInDetailsUrl)
        checkTheLinkOpensTheCorrectUrl(yourDetailsTitle, url)
        verify(analytics).logEventV3Dot1(SettingsAnalyticsViewModel.makeSignInDetailsEvent(context))
    }

    @Test
    fun helpLaunchesBrowser() = runTest {
        whenever(biometricsOptInChecker.getBiometricsOptInState()).thenReturn(flowOf(true))
        // Given the SettingsScreen Composable
        val url = resources.getString(R.string.app_helpUrl)
        checkTheLinkOpensTheCorrectUrl(helpLink, url)
        verify(analytics).logEventV3Dot1(SettingsAnalyticsViewModel.makeUsingOneLoginEvent(context))
    }

    @Test
    fun contactLaunchesBrowser() = runTest {
        whenever(biometricsOptInChecker.getBiometricsOptInState()).thenReturn(flowOf(true))

        // Given the SettingsScreen Composable
        val url = resources.getString(R.string.app_contactUrl)
        checkTheLinkOpensTheCorrectUrl(contactLink, url)
        verify(analytics).logEventV3Dot1(SettingsAnalyticsViewModel.makeContactEvent(context))
    }

    @Test
    fun privacyNoticeLaunchesBrowser() = runTest {
        whenever(biometricsOptInChecker.getBiometricsOptInState()).thenReturn(flowOf(true))

        // Given the SettingsScreen Composable
        val url = resources.getString(R.string.privacy_notice_url)
        checkTheLinkOpensTheCorrectUrl(legalLink1, url)
        verify(analytics).logEventV3Dot1(SettingsAnalyticsViewModel.makePrivacyNoticeEvent(context))
    }

    @Test
    fun accessibilityStatementLaunchesBrowser() = runTest {
        whenever(biometricsOptInChecker.getBiometricsOptInState()).thenReturn(flowOf(true))

        // Given the SettingsScreen Composable
        val url = resources.getString(R.string.app_accessibilityStatementUrl)
        checkTheLinkOpensTheCorrectUrl(legalLink3, url)
        verify(analytics).logEventV3Dot1(SettingsAnalyticsViewModel.makeAccessibilityEvent(context))
    }

    private fun checkTheLinkOpensTheCorrectUrl(linkView: SemanticsMatcher, url: String) {
        val signInURL = Uri.parse(url)
        composeTestRule.setContent {
            SettingsScreen(viewModel, analyticsViewModel)
        }
        // When clicking on the link

        composeTestRule.onNode(linkView).performScrollTo().performClick()
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
