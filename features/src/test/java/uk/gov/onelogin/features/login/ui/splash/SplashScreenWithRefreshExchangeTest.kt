package uk.gov.onelogin.features.login.ui.splash

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.TestUtils
import uk.gov.onelogin.features.appinfo.data.model.AppInfoServiceState
import uk.gov.onelogin.features.appinfo.domain.AppInfoService
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchange
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchangeResult
import uk.gov.onelogin.features.login.domain.signin.locallogin.HandleLocalLogin
import uk.gov.onelogin.features.login.ui.signin.splash.LoadingSplashScreenPreview
import uk.gov.onelogin.features.login.ui.signin.splash.SplashScreen
import uk.gov.onelogin.features.login.ui.signin.splash.SplashScreenAnalyticsViewModel
import uk.gov.onelogin.features.login.ui.signin.splash.SplashScreenPreview
import uk.gov.onelogin.features.login.ui.signin.splash.SplashScreenViewModel
import uk.gov.onelogin.features.login.ui.signin.splash.UnlockScreenPreview
import uk.gov.onelogin.features.optin.data.OptInRepository
import uk.gov.onelogin.features.optin.ui.NOTICE_TAG
import uk.gov.onelogin.features.optin.ui.OptInRequirementViewModel
import uk.gov.onelogin.features.signout.domain.SignOutUseCase
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class SplashScreenWithRefreshExchangeTest : FragmentActivityTestCase() {
    private lateinit var handleLocalLogin: HandleLocalLogin
    private lateinit var navigator: Navigator
    private lateinit var appInfoService: AppInfoService
    private lateinit var viewModel: SplashScreenViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var autoInitialiseSecureStore: AutoInitialiseSecureStore
    private lateinit var onlineChecker: OnlineChecker
    private lateinit var refreshExchange: RefreshExchange
    private lateinit var getTokenExpiry: GetTokenExpiry
    private lateinit var analyticsViewModel: SplashScreenAnalyticsViewModel
    private var repository: OptInRepository = mock()
    private lateinit var optInViewModel: OptInRequirementViewModel
    private lateinit var logo: SemanticsMatcher
    private lateinit var crownIcon: SemanticsMatcher
    private lateinit var unlockButton: SemanticsMatcher
    private lateinit var privacyNotice: SemanticsMatcher
    private lateinit var loadingIndicator: SemanticsMatcher
    private lateinit var loadingText: SemanticsMatcher
    private lateinit var loadingContentDescription: SemanticsMatcher

    @Before
    fun setUp() {
        whenever(repository.isOptInPreferenceRequired()).thenReturn(MutableStateFlow(false))
        handleLocalLogin = mock()
        navigator = mock()
        appInfoService = mock()
        autoInitialiseSecureStore = mock()
        signOutUseCase = mock()
        onlineChecker = mock()
        refreshExchange = mock()
        getTokenExpiry = mock()
        viewModel =
            SplashScreenViewModel(
                navigator,
                handleLocalLogin,
                appInfoService,
                signOutUseCase,
                autoInitialiseSecureStore,
                onlineChecker,
                refreshExchange,
                getTokenExpiry
            )
        analytics = mock()
        analyticsViewModel = SplashScreenAnalyticsViewModel(context, analytics)
        optInViewModel = OptInRequirementViewModel(repository)
        wheneverBlocking { appInfoService.get() }
            .thenReturn(AppInfoServiceState.Successful(TestUtils.appInfoData))

        logo = hasTestTag(context.getString(R.string.splashLogoTestTag))
        crownIcon = hasTestTag(resources.getString(R.string.splashCrownIconTestTag))
        unlockButton = hasTestTag(resources.getString(R.string.splashUnlockBtnTestTag))
        privacyNotice = hasTestTag(NOTICE_TAG)
        loadingIndicator =
            hasTestTag(
                resources.getString(R.string.splashLoadingSpinnerTestTag)
            )
        loadingText = hasText(resources.getString(R.string.app_splashScreenLoadingIndicatorText))
        loadingContentDescription =
            hasContentDescription(
                resources.getString(R.string.app_loading_content_desc)
            )
    }

    @Test
    fun `verify splash screen content`() {
        // Given
        composeTestRule.setContent {
            SplashScreen(viewModel, analyticsViewModel, optInViewModel)
        }

        // Then
        composeTestRule.onNode(privacyNotice).assertIsNotDisplayed()
        composeTestRule.onNode(logo).assertIsDisplayed()
        composeTestRule.onNode(crownIcon).assertIsDisplayed()
    }

    @Test
    fun `test unlock button with refresh exchange`() {
        whenever(onlineChecker.isOnline()).thenReturn(true)
        wheneverBlocking { getTokenExpiry() }.thenReturn(1L)

        wheneverBlocking { refreshExchange.getTokens(any(), any()) }.thenAnswer {
            (it.arguments[1] as (RefreshExchangeResult) -> Unit).invoke(RefreshExchangeResult.UserCancelledBioPrompt)
        }

        // When
        composeTestRule.setContent {
            SplashScreen(viewModel, analyticsViewModel, optInViewModel)
        }

        // Then
        composeTestRule.waitUntil(15000) {
            composeTestRule.onAllNodes(unlockButton).fetchSemanticsNodes().isNotEmpty()
        }

        // And
        wheneverBlocking {
            refreshExchange.getTokens(any(), any())
        }.thenAnswer {
            (it.arguments[1] as (RefreshExchangeResult) -> Unit).invoke(RefreshExchangeResult.SignInRequired)
        }

        // When
        composeTestRule.apply {
            onNode(unlockButton).performClick()
            // Test system back button to trigger analytics event being logged
            Espresso.pressBack()
        }

        // Then
        verify(navigator).goBack()
        runBlocking { verify(signOutUseCase).invoke() }
        verify(navigator).navigate(LoginRoutes.AnalyticsOptIn, false)
    }

    @Test
    fun `verify preview`() {
        composeTestRule.setContent {
            SplashScreenPreview()
        }

        composeTestRule.onNode(privacyNotice).assertIsNotDisplayed()
        composeTestRule.onNode(logo).assertIsDisplayed()
        composeTestRule.onNode(crownIcon).assertIsDisplayed()
    }

    @Test
    fun `verify unlock preview`() {
        composeTestRule.setContent {
            UnlockScreenPreview()
        }

        composeTestRule.onNode(privacyNotice).assertIsNotDisplayed()
        composeTestRule.onNode(logo).assertIsDisplayed()
        composeTestRule.onNode(crownIcon).assertIsDisplayed()
        assertTrue(
            composeTestRule.onAllNodes(unlockButton).fetchSemanticsNodes().isNotEmpty()
        )
        composeTestRule.onNode(loadingText).assertIsNotDisplayed()
        composeTestRule.onNode(loadingIndicator).assertIsNotDisplayed()
    }

    @Test
    fun `verify loading preview`() {
        composeTestRule.setContent {
            LoadingSplashScreenPreview()
        }

        composeTestRule.onNode(privacyNotice).assertIsNotDisplayed()
        composeTestRule.onNode(logo).assertIsDisplayed()
        composeTestRule.onNode(crownIcon).assertIsDisplayed()
        composeTestRule.onAllNodes(loadingText).assertCountEquals(2)
        composeTestRule.onAllNodes(loadingIndicator).assertCountEquals(2)
        composeTestRule.onAllNodes(loadingContentDescription).assertCountEquals(2)
    }

    @Test
    fun `test unlock button`() {
        composeTestRule.setContent {
            SplashScreen(viewModel, analyticsViewModel, optInViewModel)
        }
        composeTestRule.apply {
            activityRule.scenario.onActivity { activity ->
                activity.onBackPressedDispatcher.onBackPressed()
            }

            activityRule.scenario.onActivity { activity ->
                assert(activity.isFinishing)
            }
        }

        verify(analytics).logEventV3Dot1(
            SplashScreenAnalyticsViewModel.makeBackEvent(context)
        )
    }
}
