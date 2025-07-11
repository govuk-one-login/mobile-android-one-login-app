package uk.gov.onelogin.features.login.ui.welcome

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.featureflags.InMemoryFeatureFlags
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.LocalAuthManagerImpl
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsManager
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepo
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken
import uk.gov.onelogin.core.tokens.domain.save.SavePersistentId
import uk.gov.onelogin.core.tokens.domain.save.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreenAnalyticsViewModel
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.login.domain.signin.loginredirect.HandleLoginRedirect
import uk.gov.onelogin.features.login.domain.signin.remotelogin.HandleRemoteLogin
import uk.gov.onelogin.features.login.ui.signin.welcome.SignInAnalyticsViewModel
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomePreview
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreen
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreenViewModel
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@RunWith(AndroidJUnit4::class)
class WelcomeScreenTest : FragmentActivityTestCase() {
    private lateinit var deviceBiometricsManager: DeviceBiometricsManager
    private lateinit var localAuthPreferenceRepo: LocalAuthPreferenceRepo
    private lateinit var localAuthManager: LocalAuthManager
    private lateinit var tokenRepository: TokenRepository
    private lateinit var autoInitialiseSecureStore: AutoInitialiseSecureStore
    private lateinit var verifyIdToken: VerifyIdToken
    private lateinit var navigator: Navigator
    private lateinit var saveTokens: SaveTokens
    private lateinit var savePersistentId: SavePersistentId
    private lateinit var saveTokenExpiry: SaveTokenExpiry
    private lateinit var handleRemoteLogin: HandleRemoteLogin
    private lateinit var handleLoginRedirect: HandleLoginRedirect
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var featureFlags: FeatureFlags
    private lateinit var onlineChecker: OnlineChecker
    private lateinit var viewModel: WelcomeScreenViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: SignInAnalyticsViewModel
    private lateinit var loadingAnalyticsVM: LoadingScreenAnalyticsViewModel
    private val logger = SystemLogger()

    private var shouldTryAgainCalled = false

    private val signInTitle = hasText(resources.getString(R.string.app_signInTitle))
    private val signInSubTitle1 = hasText(resources.getString(R.string.app_signInBody1))

//    private val signInSubTitle2 = hasText(resources.getString(R.string.app_signInBody2))
    private val signInButton = hasText(resources.getString(R.string.app_signInButton))

    @Before
    fun setup() {
        deviceBiometricsManager = mock()
        localAuthPreferenceRepo = mock()
        tokenRepository = mock()
        autoInitialiseSecureStore = mock()
        verifyIdToken = mock()
        navigator = mock()
        saveTokens = mock()
        savePersistentId = mock()
        saveTokenExpiry = mock()
        handleRemoteLogin = mock()
        handleLoginRedirect = mock()
        signOutUseCase = mock()
        featureFlags = InMemoryFeatureFlags(
            WalletFeatureFlag.ENABLED
        )
        onlineChecker = mock()
        analytics = mock()
        localAuthManager = LocalAuthManagerImpl(
            localAuthPreferenceRepo,
            deviceBiometricsManager,
            analytics
        )
        viewModel =
            WelcomeScreenViewModel(
                context,
                localAuthManager,
                tokenRepository,
                autoInitialiseSecureStore,
                verifyIdToken,
                navigator,
                saveTokens,
                savePersistentId,
                saveTokenExpiry,
                handleRemoteLogin,
                handleLoginRedirect,
                signOutUseCase,
                logger,
                featureFlags,
                onlineChecker
            )
        analyticsViewModel = SignInAnalyticsViewModel(context, analytics)
        loadingAnalyticsVM = LoadingScreenAnalyticsViewModel(context, analytics)
        shouldTryAgainCalled = false
    }

    @Suppress("ForbiddenComment")
    @Test
    fun verifyComponents() {
        composeTestRule.setContent {
            WelcomeScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel,
                loadingAnalyticsViewModel = loadingAnalyticsVM
            )
        }

        composeTestRule.onNode(signInTitle).assertIsDisplayed()
        composeTestRule.onNode(signInSubTitle1).assertIsDisplayed()
        // TODO Fix breaking line below in buildRelease and StagingRelease flavours
        // composeTestRule.onNode(signInSubTitle2).assertIsDisplayed()
        composeTestRule.onNode(signInButton).assertIsDisplayed()
        // TODO: Add testTag to the icon in mobile ui to be able to test the icon on CentreAlignedScreen when contentDescription is empty
    }

    @Test
    fun opensWebLoginViaCustomTab() =
        runBlocking {
            whenever(onlineChecker.isOnline()).thenReturn(true)
            composeTestRule.setContent {
                WelcomeScreen(
                    analyticsViewModel = analyticsViewModel,
                    viewModel = viewModel,
                    loadingAnalyticsViewModel = loadingAnalyticsVM
                )
            }

            whenWeClickSignIn()

            verify(handleRemoteLogin).login(any(), any())
        }

    @Test
    fun shouldTryAgainCalledOnPageLoad() {
        composeTestRule.setContent {
            WelcomeScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel,
                loadingAnalyticsViewModel = loadingAnalyticsVM,
                shouldTryAgain = {
                    shouldTryAgainCalled = true
                    false
                }
            )
        }
        assertTrue(shouldTryAgainCalled)
    }

    @Test
    fun loginFiresAutomaticallyIfOnlineAndShouldTryAgainIsTrue() =
        runBlocking {
            whenever(onlineChecker.isOnline()).thenReturn(true)
            composeTestRule.setContent {
                WelcomeScreen(
                    analyticsViewModel = analyticsViewModel,
                    viewModel = viewModel,
                    loadingAnalyticsViewModel = loadingAnalyticsVM,
                    shouldTryAgain = {
                        true
                    }
                )
            }

            verify(handleRemoteLogin).login(any(), any())
        }

    @Test
    fun navigateToErrorScreenIfNotOnlineAndShouldTryAgainIsTrue() =
        runBlocking {
            whenever(onlineChecker.isOnline()).thenReturn(false)
            composeTestRule.setContent {
                WelcomeScreen(
                    analyticsViewModel = analyticsViewModel,
                    viewModel = viewModel,
                    loadingAnalyticsViewModel = loadingAnalyticsVM,
                    shouldTryAgain = {
                        true
                    }
                )
            }
            verify(navigator).navigate(ErrorRoutes.Offline)
        }

    @Test
    fun opensNetworkErrorScreen() {
        givenWeAreOffline()

        whenWeClickSignIn()

        itOpensErrorScreen()
    }

    @Test
    fun screenViewAnalyticsLogOnResume() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val event = SignInAnalyticsViewModel.makeWelcomeViewEvent(context)
        composeTestRule.setContent {
            WelcomeScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel,
                loadingAnalyticsViewModel = loadingAnalyticsVM
            )
        }

        verify(analytics).logEventV3Dot1(event)
    }

    @Test
    fun signInAnalyticsLogOnSignInButton() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val event = SignInAnalyticsViewModel.makeSignInEvent(context)
        whenever(onlineChecker.isOnline()).thenReturn(true)
        composeTestRule.setContent {
            WelcomeScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel,
                loadingAnalyticsViewModel = loadingAnalyticsVM
            )
        }
        whenWeClickSignIn()
        verify(analytics).logEventV3Dot1(event)
    }

    @Test
    fun testBackButton() {
        composeTestRule.setContent {
            whenever(onlineChecker.isOnline()).thenReturn(true)
            WelcomeScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel,
                loadingAnalyticsViewModel = loadingAnalyticsVM,
                shouldTryAgain = {
                    shouldTryAgainCalled = true
                    false
                }
            )
        }

        composeTestRule.apply {
            activityRule.scenario.onActivity { activity ->
                activity.onBackPressedDispatcher.onBackPressed()
                assert(activity.isFinishing)
            }
        }
    }

    private fun whenWeClickSignIn() {
        composeTestRule.onNode(signInButton).performClick()
    }

    private fun givenWeAreOffline() {
        whenever(onlineChecker.isOnline()).thenReturn(false)
        composeTestRule.setContent {
            WelcomeScreen(
                analyticsViewModel = analyticsViewModel,
                viewModel = viewModel,
                loadingAnalyticsViewModel = loadingAnalyticsVM
            )
        }
    }

    private fun itOpensErrorScreen() {
        verify(navigator).navigate(ErrorRoutes.Offline)
    }

    @Test
    fun previewTest() {
        composeTestRule.setContent {
            WelcomePreview()
        }
    }
}
