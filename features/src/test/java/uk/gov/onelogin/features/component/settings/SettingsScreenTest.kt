package uk.gov.onelogin.features.component.settings

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
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.UriMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.core.navigation.data.SettingsRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.idtoken.email.ExtractEmail
import uk.gov.onelogin.core.ui.components.DIVIDER_TEST_TAG
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.optin.data.OptInRepository
import uk.gov.onelogin.features.optin.ui.NOTICE_TAG
import uk.gov.onelogin.features.settings.ui.AboutTheAppSection
import uk.gov.onelogin.features.settings.ui.PreferenceToggleRow
import uk.gov.onelogin.features.settings.ui.SettingsAnalyticsViewModel
import uk.gov.onelogin.features.settings.ui.SettingsScreen
import uk.gov.onelogin.features.settings.ui.SettingsScreenOptInNoShowBiometricsPreview
import uk.gov.onelogin.features.settings.ui.SettingsScreenOptOutShowBiometricsPreview
import uk.gov.onelogin.features.settings.ui.SettingsScreenViewModel
import uk.gov.onelogin.features.wallet.data.WalletRepository

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest : FragmentActivityTestCase() {
    private val optInRepository: OptInRepository = Mockito.mock()
    private lateinit var navigator: Navigator
    private lateinit var extractEmail: ExtractEmail
    private lateinit var localAuthManager: LocalAuthManager
    private lateinit var tokenRepository: TokenRepository
    private lateinit var mockWalletRepository: WalletRepository
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
    private lateinit var addDocumentsLink: SemanticsMatcher
    private lateinit var termsAndConditionsLink: SemanticsMatcher
    private lateinit var proveIdentityLink: SemanticsMatcher

    @Before
    fun setUp() {
        whenever(optInRepository.isOptInPreferenceRequired())
            .thenReturn(MutableStateFlow(false))
        whenever(optInRepository.hasAnalyticsOptIn()).thenReturn(MutableStateFlow(false))
        navigator = Mockito.mock()
        extractEmail = Mockito.mock()
        tokenRepository = Mockito.mock()
        localAuthManager = Mockito.mock()
        mockWalletRepository = Mockito.mock()
        viewModel =
            SettingsScreenViewModel(
                optInRepository,
                navigator,
                localAuthManager,
                mockWalletRepository,
                tokenRepository,
                extractEmail
            )
        analytics = Mockito.mock()
        analyticsViewModel = SettingsAnalyticsViewModel(context, analytics)
        yourDetailsHeader = hasText(resources.getString(R.string.app_settingsSubtitle1))
        yourDetailsTitle = hasText(resources.getString(R.string.app_settingsSignInDetailsLink))
        yourDetailsSubTitle =
            hasText(
                resources.getString(R.string.app_settingSignInDetailsFootnote)
            )
        legalLink1 = hasText(resources.getString(R.string.app_privacyNoticeLink2))
        legalLink2 = hasText(resources.getString(R.string.app_openSourceLicences))
        legalLink3 = hasText(resources.getString(R.string.app_accessibilityStatement))
        helpLink = hasText(resources.getString(R.string.app_proveYourIdentityLink))
        contactLink = hasText(resources.getString(R.string.app_contactLink))
        aboutTheAppSwitch = hasTestTag(resources.getString(R.string.optInSwitchTestTag))
        aboutTheAppSubTitle =
            hasText(
                resources.getString(R.string.app_settingsAnalyticsToggleFootnote),
                substring = true
            )
        aboutTheAppPrivacyLink = hasTestTag(NOTICE_TAG)
        signOutButton = hasText(resources.getString(R.string.app_signOutButton))
        openSourceLicensesButton = hasText(resources.getString(R.string.app_openSourceLicences))
        biometricsOptIn = hasText(context.getString(R.string.app_settingsBiometricsField))
        Intents.init()
        Intents.intending(CoreMatchers.not(IntentMatchers.isInternal())).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        )
        addDocumentsLink = hasText(resources.getString(R.string.app_addDocumentsLink))
        termsAndConditionsLink = hasText(resources.getString(R.string.app_termsAndConditionsLink))
        proveIdentityLink = hasText(resources.getString(R.string.app_proveYourIdentityLink))
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun yourDetailsGroupDisplayed() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(true)
            val settingsViewModel =
                SettingsScreenViewModel(
                    optInRepository,
                    navigator,
                    localAuthManager,
                    mockWalletRepository,
                    tokenRepository,
                    extractEmail
                )
            composeTestRule.setContent {
                SettingsScreen(settingsViewModel, analyticsViewModel)
            }
            composeTestRule.onNodeWithTag(DIVIDER_TEST_TAG).assertIsDisplayed()
            composeTestRule.onNode(yourDetailsHeader).assertIsDisplayed()
            composeTestRule.onNode(yourDetailsTitle).assertIsDisplayed()
            composeTestRule.onNode(yourDetailsSubTitle).assertIsDisplayed()
            composeTestRule.onNode(proveIdentityLink).assertIsDisplayed()
            composeTestRule.onNode(addDocumentsLink).assertIsDisplayed()
            verify(analytics).logEventV3Dot1(
                SettingsAnalyticsViewModel.Companion.makeSettingsViewEvent(
                    context
                )
            )
        }

    @Test
    fun legalGroupDisplayed() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(true)
            val settingsViewModel =
                SettingsScreenViewModel(
                    optInRepository,
                    navigator,
                    localAuthManager,
                    mockWalletRepository,
                    tokenRepository,
                    extractEmail
                )
            composeTestRule.setContent {
                SettingsScreen(settingsViewModel, analyticsViewModel)
            }
            composeTestRule.onNode(legalLink1).performScrollTo().assertIsDisplayed()
            composeTestRule.onNode(legalLink2).performScrollTo().assertIsDisplayed()
            composeTestRule.onNode(legalLink3).performScrollTo().assertIsDisplayed()
            composeTestRule.onNode(termsAndConditionsLink).performScrollTo().assertIsDisplayed()
        }

    @Test
    fun aboutTheAppSectionDisplayed() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(true)

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
    fun toggleSwitchCallOnToggleClickEvent() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(true)

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
    fun privacyNoticeInAboutTheAppSectionLaunchesBrowser() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(true)

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
            composeTestRule
                .onNode(aboutTheAppPrivacyLink, useUnmergedTree = true)
                .performTouchInput {
                    click(bottomRight.copy(x = bottomRight.x - 10f))
                }
            Assert.assertTrue(privacyNoticeClicked)
        }

    @Test
    fun biometricsDisplayedAndOnClickWorks() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(true)

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
    fun biometricsNotDisplayed() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(false)

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
    fun biometricsEnabledBiometricsFeatureFlagDisabled() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(true)

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
    fun signOutCta() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(true)

            composeTestRule.setContent {
                SettingsScreen(viewModel, analyticsViewModel)
            }
            composeTestRule.onNode(signOutButton).performScrollTo().performClick()
            verify(navigator).navigate(SignOutRoutes.Start)
            verify(analytics).logEventV3Dot1(
                SettingsAnalyticsViewModel.Companion.makeSignOutEvent(
                    context
                )
            )
        }

    @Test
    fun openSourceLicensesCta() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(true)

            composeTestRule.setContent {
                SettingsScreen(viewModel, analyticsViewModel)
            }
            composeTestRule.onNode(openSourceLicensesButton).performScrollTo().performClick()
            verify(navigator).navigate(SettingsRoutes.Ossl)
            verify(analytics).logEventV3Dot1(
                SettingsAnalyticsViewModel.Companion.makeOpenSourceEvent(
                    context
                )
            )
        }

    @Test
    fun signInLaunchesBrowser() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(true)

            // Given the SettingsScreen Composable
            val url = resources.getString(R.string.app_manageSignInDetailsUrl)
            checkTheLinkOpensTheCorrectUrl(yourDetailsTitle, url)
            verify(analytics).logEventV3Dot1(
                SettingsAnalyticsViewModel.Companion.makeSignInDetailsEvent(
                    context
                )
            )
        }

    @Test
    fun helpLaunchesBrowser() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(true)
            // Given the SettingsScreen Composable
            val url = resources.getString(R.string.app_helpUrl)
            checkTheLinkOpensTheCorrectUrl(helpLink, url)
            verify(analytics).logEventV3Dot1(
                SettingsAnalyticsViewModel.Companion.makeUsingOneLoginEvent(
                    context
                )
            )
        }

    @Test
    fun contactLaunchesBrowser() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(true)

            // Given the SettingsScreen Composable
            val url = resources.getString(R.string.app_contactUrl)
            checkTheLinkOpensTheCorrectUrl(contactLink, url)
            verify(analytics).logEventV3Dot1(
                SettingsAnalyticsViewModel.Companion.makeContactEvent(
                    context
                )
            )
        }

    @Test
    fun privacyNoticeLaunchesBrowser() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(true)

            // Given the SettingsScreen Composable
            val url = resources.getString(R.string.privacy_notice_url)
            checkTheLinkOpensTheCorrectUrl(legalLink1, url)
            verify(analytics).logEventV3Dot1(
                SettingsAnalyticsViewModel.Companion.makePrivacyNoticeEvent(
                    context
                )
            )
        }

    @Test
    fun accessibilityStatementLaunchesBrowser() =
        runTest {
            whenever(localAuthManager.biometricsAvailable()).thenReturn(true)

            // Given the SettingsScreen Composable
            val url = resources.getString(R.string.app_accessibilityStatementUrl)
            checkTheLinkOpensTheCorrectUrl(legalLink3, url)
            verify(analytics).logEventV3Dot1(
                SettingsAnalyticsViewModel.Companion.makeAccessibilityEvent(
                    context
                )
            )
        }

    @Test
    fun addDocumentsLaunchesBrowser() =
        runTest {
            val url = resources.getString(R.string.app_add_document_url)
            val settingsViewModel =
                SettingsScreenViewModel(
                    optInRepository,
                    navigator,
                    localAuthManager,
                    mockWalletRepository,
                    tokenRepository,
                    extractEmail
                )
            checkTheLinkOpensTheCorrectUrl(
                addDocumentsLink,
                url,
                settingsViewModel
            )
            verify(analytics).logEventV3Dot1(
                SettingsAnalyticsViewModel.Companion.makeAddDocumentsEvent(
                    context
                )
            )
        }

    @Test
    fun termsAndConditionsLaunchesBrowser() =
        runTest {
            val settingsViewModel =
                SettingsScreenViewModel(
                    optInRepository,
                    navigator,
                    localAuthManager,
                    mockWalletRepository,
                    tokenRepository,
                    extractEmail
                )
            val url = resources.getString(R.string.app_terms_and_conditions_url)
            checkTheLinkOpensTheCorrectUrl(
                termsAndConditionsLink,
                url,
                settingsViewModel
            )
            verify(analytics).logEventV3Dot1(
                SettingsAnalyticsViewModel.Companion.makeTermsAndConditionsEvent(context)
            )
        }

    @Test
    fun optOutBiometricsPreview() =
        runTest {
            composeTestRule.setContent {
                SettingsScreenOptOutShowBiometricsPreview()
            }

            composeTestRule.onNode(yourDetailsHeader).assertIsDisplayed()
            composeTestRule.onNode(yourDetailsTitle).assertIsDisplayed()
            composeTestRule.onNode(yourDetailsSubTitle).assertIsDisplayed()
        }

    @Test
    fun optInNoBiometricsPreview() =
        runTest {
            composeTestRule.setContent {
                SettingsScreenOptInNoShowBiometricsPreview()
            }

            composeTestRule.onNode(yourDetailsHeader).assertIsDisplayed()
            composeTestRule.onNode(yourDetailsTitle).assertIsDisplayed()
            composeTestRule.onNode(yourDetailsSubTitle).assertIsDisplayed()
        }

    private fun checkTheLinkOpensTheCorrectUrl(
        linkView: SemanticsMatcher,
        url: String,
        settingsViewModel: SettingsScreenViewModel = viewModel
    ) {
        val signInURL = Uri.parse(url)
        composeTestRule.setContent {
            SettingsScreen(settingsViewModel, analyticsViewModel)
        }
        // When clicking on the link

        composeTestRule.onNode(linkView).performScrollTo().performClick()
        // Then open a browser with the correct URL
        val host = signInURL.host
        val path = signInURL.path
        val scheme = signInURL.scheme
        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                IntentMatchers.hasData(UriMatchers.hasHost(host)),
                IntentMatchers.hasData(UriMatchers.hasPath(path)),
                IntentMatchers.hasData(UriMatchers.hasScheme(scheme))
            )
        )
    }
}
