package uk.gov.onelogin.features.component.signout.info

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.fragment.app.FragmentActivity
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.AdditionalAnswers
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.core.localauth.domain.LocalAuthPrefResetUseCase
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreenAnalyticsViewModel
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.login.LoginViewModel
import uk.gov.onelogin.features.login.domain.signin.remotelogin.RemoteLogin
import uk.gov.onelogin.features.signout.domain.SignOutUseCase
import uk.gov.onelogin.features.signout.ui.info.SignedOutInfoAnalyticsViewModel
import uk.gov.onelogin.features.signout.ui.info.SignedOutInfoScreen
import uk.gov.onelogin.features.signout.ui.info.SignedOutInfoViewModel

@RunWith(AndroidJUnit4::class)
class SignedOutInfoScreenTest : FragmentActivityTestCase() {
    private lateinit var mockFragmentActivity: FragmentActivity
    private lateinit var getPersistentId: GetPersistentId
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var localAuthPrefResetUseCase: LocalAuthPrefResetUseCase
    private lateinit var tokenRepository: TokenRepository
    private val logger = MemorisedLogger()
    private lateinit var navigator: Navigator
    private lateinit var remoteLogin: RemoteLogin
    private lateinit var onlineChecker: OnlineChecker
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: SignedOutInfoAnalyticsViewModel
    private lateinit var loadingAnalyticsViewModel: LoadingScreenAnalyticsViewModel
    private lateinit var viewModel: SignedOutInfoViewModel
    private var shouldTryAgainCalled = false

    private val signedOutTitle = hasText(resources.getString(R.string.app_youveBeenSignedOutTitle))
    private val signedOutBody1 = hasText(resources.getString(R.string.app_youveBeenSignedOutBody1))
    private val signedOutBody2 = hasText(resources.getString(R.string.app_youveBeenSignedOutBody2))
    private val signedOutButton =
        hasText(resources.getString(R.string.app_SignInWithGovUKOneLoginButton))

    @Before
    @Suppress("LongMethod")
    fun setup() =
        runBlocking {
            mockFragmentActivity = mock()
            signOutUseCase = mock()
            localAuthPrefResetUseCase = mock()
            tokenRepository = mock()
            getPersistentId = mock()
            navigator = mock()
            remoteLogin = mock()
            onlineChecker = mock()
            analytics = mock()
            viewModel =
                SignedOutInfoViewModel(
                    navigator,
                    tokenRepository,
                )
            loginViewModel =
                LoginViewModel(
                    navigator,
                    onlineChecker,
                    remoteLogin,
                    getPersistentId,
                    signOutUseCase,
                    localAuthPrefResetUseCase,
                    logger
                )
            analyticsViewModel = SignedOutInfoAnalyticsViewModel(context, analytics)
            loadingAnalyticsViewModel = LoadingScreenAnalyticsViewModel(context, analytics)
        }

    @Test
    fun verifyScreenDisplayed() {
        composeTestRule.setContent {
            SignedOutInfoScreen(
                loginViewModel,
                viewModel,
                analyticsViewModel,
                loadingAnalyticsViewModel
            )
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
            whenever(getPersistentId.invoke()).thenReturn("persistentId")

            composeTestRule.setContent {
                SignedOutInfoScreen(
                    loginViewModel,
                    viewModel,
                    analyticsViewModel,
                    loadingAnalyticsViewModel
                )
            }

            whenWeClickSignIn()

            verify(remoteLogin).start(any())
        }

    @Test
    fun noPersistentId_OpensSignInScreen() =
        runBlocking {
            whenever(onlineChecker.isOnline()).thenReturn(true)

            composeTestRule.setContent {
                SignedOutInfoScreen(
                    loginViewModel,
                    viewModel,
                    analyticsViewModel,
                    loadingAnalyticsViewModel
                )
            }

            whenWeClickSignIn()

            verify(signOutUseCase).invoke()
            verify(navigator).navigate(SignOutRoutes.ReAuthError, true)
        }

    @Test
    fun shouldTryAgainCalledOnPageLoad() {
        composeTestRule.setContent {
            SignedOutInfoScreen(
                loginViewModel,
                viewModel,
                analyticsViewModel,
                loadingAnalyticsViewModel,
                shouldTryAgain = {
                    shouldTryAgainCalled = true
                    false
                }
            )
        }
        Assert.assertTrue(shouldTryAgainCalled)
    }

    @Test
    fun loginFiresAutomaticallyIfOnlineAndShouldTryAgainIsTrue(): Unit =
        runBlocking {
            whenever(onlineChecker.isOnline()).thenReturn(true)
            whenever(getPersistentId.invoke()).thenReturn("persistentId")

            composeTestRule.setContent {
                SignedOutInfoScreen(
                    loginViewModel,
                    viewModel,
                    analyticsViewModel,
                    loadingAnalyticsViewModel,
                    shouldTryAgain = {
                        true
                    }
                )
            }

            verify(remoteLogin).start(any())
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
                    loadingAnalyticsViewModel,
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
        val event = SignedOutInfoAnalyticsViewModel.Companion.makeSignedOutInfoViewEvent(context)
        composeTestRule.setContent {
            SignedOutInfoScreen(
                loginViewModel,
                viewModel,
                analyticsViewModel,
                loadingAnalyticsViewModel
            )
        }

        verify(analytics).logEventV3Dot1(event)
    }

    @Test
    fun reAuthAnalyticsLogOnSignInButton() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val event = SignedOutInfoAnalyticsViewModel.Companion.makeReAuthEvent(context)
        whenever(onlineChecker.isOnline()).thenReturn(true)
        composeTestRule.setContent {
            SignedOutInfoScreen(
                loginViewModel,
                viewModel,
                analyticsViewModel,
                loadingAnalyticsViewModel
            )
        }
        whenWeClickSignIn()
        verify(analytics).logEventV3Dot1(event)
    }

    @Ignore("Check if there is a way to get the loading screen show")
    @Test
    fun loadingScreenDisplaysOnButtonClick() {
        whenever(onlineChecker.isOnline()).thenReturn(true)
        composeTestRule.setContent {
            SignedOutInfoScreen(
                loginViewModel,
                viewModel,
                analyticsViewModel,
                loadingAnalyticsViewModel
            )
        }

        whenWeClickSignIn()

        wheneverBlocking { remoteLogin.start(any()) }.thenAnswer(
            AdditionalAnswers.answersWithDelay(
                1000
            ) { _: InvocationOnMock? -> null }
        )

        composeTestRule.waitUntil {
            composeTestRule.onNodeWithTag("loadingScreen_progressIndicator").isDisplayed()
        }
    }

    private fun whenWeClickSignIn() {
        composeTestRule.onNode(signedOutButton).performClick()
    }

    private fun givenWeAreOffline() {
        whenever(onlineChecker.isOnline()).thenReturn(false)
        composeTestRule.setContent {
            SignedOutInfoScreen(
                loginViewModel,
                viewModel,
                analyticsViewModel,
                loadingAnalyticsViewModel
            )
        }
    }

    private fun itOpensErrorScreen() {
        verify(navigator).navigate(ErrorRoutes.Offline)
    }
}
