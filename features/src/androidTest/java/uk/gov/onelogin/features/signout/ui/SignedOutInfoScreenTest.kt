package uk.gov.onelogin.features.signout.ui

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.core.biometrics.domain.BiometricPreferenceHandler
import uk.gov.onelogin.core.biometrics.domain.CredentialChecker
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.tokens.domain.save.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
import uk.gov.onelogin.features.TestCase
import uk.gov.onelogin.features.login.domain.signin.loginredirect.HandleLoginRedirect
import uk.gov.onelogin.features.login.domain.signin.remotelogin.HandleRemoteLogin
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreenViewModel
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

class SignedOutInfoScreenTest : TestCase() {
    private lateinit var credChecker: CredentialChecker
    private lateinit var biometricPreferenceHandler: BiometricPreferenceHandler
    private lateinit var tokenRepository: TokenRepository
    private lateinit var autoInitialiseSecureStore: AutoInitialiseSecureStore
    private lateinit var verifyIdToken: VerifyIdToken
    private lateinit var navigator: Navigator
    private lateinit var saveTokens: SaveTokens
    private lateinit var saveTokenExpiry: SaveTokenExpiry
    private lateinit var handleRemoteLogin: HandleRemoteLogin
    private lateinit var handleLoginRedirect: HandleLoginRedirect
    private lateinit var onlineChecker: OnlineChecker
    private lateinit var loginViewModel: WelcomeScreenViewModel
    private lateinit var getPersistentId: GetPersistentId
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var viewModel: SignedOutInfoViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: SignedOutInfoAnalyticsViewModel
    private var shouldTryAgainCalled = false

    private val signedOutTitle = hasText(resources.getString(R.string.app_youveBeenSignedOutTitle))
    private val signedOutBody1 = hasText(resources.getString(R.string.app_youveBeenSignedOutBody1))
    private val signedOutBody2 = hasText(resources.getString(R.string.app_youveBeenSignedOutBody2))
    private val signedOutButton =
        hasText(resources.getString(R.string.app_SignInWithGovUKOneLoginButton))

    @Before
    fun setup() =
        runBlocking {
            credChecker = mock()
            biometricPreferenceHandler = mock()
            tokenRepository = mock()
            autoInitialiseSecureStore = mock()
            verifyIdToken = mock()
            navigator = mock()
            saveTokens = mock()
            saveTokenExpiry = mock()
            handleRemoteLogin = mock()
            handleLoginRedirect = mock()
            onlineChecker = mock()
            loginViewModel =
                WelcomeScreenViewModel(
                    context,
                    credChecker,
                    biometricPreferenceHandler,
                    tokenRepository,
                    autoInitialiseSecureStore,
                    verifyIdToken,
                    navigator,
                    saveTokens,
                    saveTokenExpiry,
                    handleRemoteLogin,
                    handleLoginRedirect,
                    onlineChecker
                )
            getPersistentId = mock()
            signOutUseCase = mock()
            viewModel =
                SignedOutInfoViewModel(
                    navigator,
                    tokenRepository,
                    saveTokens,
                    getPersistentId,
                    signOutUseCase
                )
            analytics = mock()
            analyticsViewModel = SignedOutInfoAnalyticsViewModel(context, analytics)
            shouldTryAgainCalled = false
        }

    @Test
    fun verifyScreenDisplayed() {
        composeTestRule.setContent {
            SignedOutInfoScreen(loginViewModel, viewModel, analyticsViewModel)
        }

        composeTestRule.apply {
            onNode(signedOutTitle).assertIsDisplayed()
            onNode(signedOutBody1).assertIsDisplayed()
            onNode(signedOutBody2).assertIsDisplayed()
        }
    }

    @Test
    fun opensWebLoginViaCustomTab() =
        runBlocking {
            whenever(onlineChecker.isOnline()).thenReturn(true)

            composeTestRule.setContent {
                SignedOutInfoScreen(loginViewModel, viewModel, analyticsViewModel)
            }

            whenWeClickSignIn()

            verify(handleRemoteLogin).login(any(), any())
            verify(handleLoginRedirect).handle(any(), any(), any())
        }

    @Test
    fun noPersistentId_OpensSignInScreen() =
        runBlocking {
            whenever(onlineChecker.isOnline()).thenReturn(true)

            composeTestRule.setContent {
                SignedOutInfoScreen(loginViewModel, viewModel, analyticsViewModel)
            }

            whenWeClickSignIn()

            verify(signOutUseCase).invoke(any())
            verify(navigator).navigate(SignOutRoutes.ReAuthError, true)
        }

    @Test
    fun shouldTryAgainCalledOnPageLoad() {
        composeTestRule.setContent {
            SignedOutInfoScreen(
                loginViewModel,
                viewModel,
                analyticsViewModel,
                shouldTryAgain = {
                    shouldTryAgainCalled = true
                    false
                }
            )
        }
        assertTrue(shouldTryAgainCalled)
    }

    @Test
    fun loginFiresAutomaticallyIfOnlineAndShouldTryAgainIsTrue(): Unit =
        runBlocking {
            whenever(onlineChecker.isOnline()).thenReturn(true)

            composeTestRule.setContent {
                SignedOutInfoScreen(
                    loginViewModel,
                    viewModel,
                    analyticsViewModel,
                    shouldTryAgain = {
                        true
                    }
                )
            }

            verify(handleRemoteLogin).login(any(), any())
            verify(handleLoginRedirect).handle(any(), any(), any())
        }

    @Test
    fun loginFiresAutomaticallyButOffline() =
        runBlocking {
            whenever(onlineChecker.isOnline()).thenReturn(false)
            composeTestRule.setContent {
                SignedOutInfoScreen(
                    loginViewModel,
                    viewModel,
                    analyticsViewModel,
                    shouldTryAgain = {
                        true
                    }
                )
            }

            itOpensErrorScreen()
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
        val event = SignedOutInfoAnalyticsViewModel.makeSignedOutInfoViewEvent(context)
        composeTestRule.setContent {
            SignedOutInfoScreen(loginViewModel, viewModel, analyticsViewModel)
        }

        verify(analytics).logEventV3Dot1(event)
    }

    @Test
    fun reAuthAnalyticsLogOnSignInButton() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val event = SignedOutInfoAnalyticsViewModel.makeReAuthEvent(context)
        whenever(onlineChecker.isOnline()).thenReturn(true)
        composeTestRule.setContent {
            SignedOutInfoScreen(loginViewModel, viewModel, analyticsViewModel)
        }
        whenWeClickSignIn()
        verify(analytics).logEventV3Dot1(event)
    }

    @Test
    fun loadingScreenDisplaysOnButtonClick() {
        whenever(onlineChecker.isOnline()).thenReturn(true)
        composeTestRule.setContent {
            SignedOutInfoScreen(loginViewModel, viewModel, analyticsViewModel)
        }

        whenWeClickSignIn()

        composeTestRule.onNodeWithTag("loadingScreen_progressIndicator").assertIsDisplayed()
    }

    private fun whenWeClickSignIn() {
        composeTestRule.onNode(signedOutButton).performClick()
    }

    private fun givenWeAreOffline() {
        whenever(onlineChecker.isOnline()).thenReturn(false)
        composeTestRule.setContent {
            SignedOutInfoScreen(loginViewModel, viewModel, analyticsViewModel)
        }
    }

    private fun itOpensErrorScreen() {
        verify(navigator).navigate(ErrorRoutes.Offline)
    }
}
