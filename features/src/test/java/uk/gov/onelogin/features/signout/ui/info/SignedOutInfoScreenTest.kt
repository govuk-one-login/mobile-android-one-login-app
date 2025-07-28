package uk.gov.onelogin.features.signout.ui.info

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.fragment.app.FragmentActivity
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.AdditionalAnswers
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking
import uk.gov.android.authentication.login.AuthenticationError
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
import uk.gov.onelogin.core.counter.Counter
import uk.gov.onelogin.core.localauth.domain.LocalAuthPrefResetUseCase
import uk.gov.onelogin.core.localauth.domain.LocalAuthPrefResetUseCaseImpl
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepo
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.tokens.domain.save.SavePersistentId
import uk.gov.onelogin.core.tokens.domain.save.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.domain.save.SaveTokens
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreenAnalyticsViewModel
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import uk.gov.onelogin.features.login.domain.signin.loginredirect.HandleLoginRedirect
import uk.gov.onelogin.features.login.domain.signin.remotelogin.HandleRemoteLogin
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreenViewModel
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@RunWith(AndroidJUnit4::class)
class SignedOutInfoScreenTest : FragmentActivityTestCase() {
    private lateinit var localAuthPreferenceRepo: LocalAuthPreferenceRepo
    private lateinit var deviceBiometricsManager: DeviceBiometricsManager
    private lateinit var localAuthManager: LocalAuthManager
    private lateinit var tokenRepository: TokenRepository
    private lateinit var autoInitialiseSecureStore: AutoInitialiseSecureStore
    private lateinit var verifyIdToken: VerifyIdToken
    private lateinit var navigator: Navigator
    private lateinit var saveTokens: SaveTokens
    private lateinit var saveTokenExpiry: SaveTokenExpiry
    private lateinit var savePersistentId: SavePersistentId
    private lateinit var handleRemoteLogin: HandleRemoteLogin
    private lateinit var handleLoginRedirect: HandleLoginRedirect
    private lateinit var featureFlags: FeatureFlags
    private lateinit var onlineChecker: OnlineChecker
    private lateinit var loginViewModel: WelcomeScreenViewModel
    private lateinit var getPersistentId: GetPersistentId
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var viewModel: SignedOutInfoViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: SignedOutInfoAnalyticsViewModel
    private lateinit var loadingAnalyticsViewModel: LoadingScreenAnalyticsViewModel
    private lateinit var localAuthPrefResetUseCase: LocalAuthPrefResetUseCase
    private lateinit var errorCounter: Counter
    private lateinit var mockFragmentActivity: FragmentActivity
    private val logger = SystemLogger()
    private var shouldTryAgainCalled = false

    private val signedOutTitle = hasText(resources.getString(R.string.app_youveBeenSignedOutTitle))
    private val signedOutBody1 = hasText(resources.getString(R.string.app_youveBeenSignedOutBody1))
    private val signedOutBody2 = hasText(resources.getString(R.string.app_youveBeenSignedOutBody2))
    private val signedOutButton =
        hasText(resources.getString(R.string.app_SignInWithGovUKOneLoginButton))

    private val serverError = AuthenticationError(
        "server_error",
        AuthenticationError.ErrorType.SERVER_ERROR
    )

    @Before
    @Suppress("LongMethod")
    fun setup() = runBlocking {
        localAuthPreferenceRepo = mock()
        deviceBiometricsManager = mock()
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
        errorCounter = mock()
        localAuthManager = LocalAuthManagerImpl(
            localAuthPreferenceRepo,
            deviceBiometricsManager,
            analytics
        )
        localAuthPrefResetUseCase = LocalAuthPrefResetUseCaseImpl(
            localAuthPreferenceRepo,
            localAuthManager
        )
        loginViewModel = WelcomeScreenViewModel(
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
            onlineChecker,
            errorCounter
        )
        getPersistentId = mock()
        signOutUseCase = mock()
        viewModel = SignedOutInfoViewModel(
            navigator,
            tokenRepository,
            saveTokens,
            getPersistentId,
            signOutUseCase,
            localAuthPrefResetUseCase,
            logger
        )
        analyticsViewModel = SignedOutInfoAnalyticsViewModel(context, analytics)
        loadingAnalyticsViewModel = LoadingScreenAnalyticsViewModel(context, analytics)
        shouldTryAgainCalled = false
        mockFragmentActivity = mock()
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
    fun opensWebLoginViaCustomTab() = runBlocking {
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

        verify(handleRemoteLogin).login(any(), any())
    }

    @Test
    fun noPersistentId_OpensSignInScreen() = runBlocking {
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
    fun opensSignInScreenServerErrorAttempts1RecoverableError() = runBlocking {
        whenever(onlineChecker.isOnline()).thenReturn(true)
        val mockIntent: Intent = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
        whenever(handleLoginRedirect.handle(eq(mockIntent), any(), any()))
            .thenAnswer {
                (it.arguments[1] as (error: AuthenticationError) -> Unit)
                    .invoke(serverError)
            }
        whenever(verifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
            .thenReturn(true)
        whenever(errorCounter.getValue()).thenReturn(1)
        loginViewModel.handleActivityResult(
            mockIntent,
            true,
            activity = mockFragmentActivity
        )

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
        verify(navigator).navigate(LoginRoutes.SignInRecoverableError, true)
    }

    @Test
    fun opensSignInScreenServerErrorAttempts3UnRecoverableError() = runBlocking {
        whenever(onlineChecker.isOnline()).thenReturn(true)
        val mockIntent: Intent = mock()
        val mockUri: Uri = mock()

        whenever(mockIntent.data).thenReturn(mockUri)
        whenever(deviceBiometricsManager.isDeviceSecure()).thenReturn(true)
        whenever(handleLoginRedirect.handle(eq(mockIntent), any(), any()))
            .thenAnswer {
                (it.arguments[1] as (error: AuthenticationError) -> Unit)
                    .invoke(serverError)
            }
        whenever(verifyIdToken.invoke(eq("testIdToken"), eq("testUrl")))
            .thenReturn(true)
        whenever(errorCounter.getValue()).thenReturn(3)
        loginViewModel.handleActivityResult(
            mockIntent,
            true,
            activity = mockFragmentActivity
        )

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
        verify(navigator).navigate(LoginRoutes.SignInUnrecoverableError, true)
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
        assertTrue(shouldTryAgainCalled)
    }

    @Test
    fun loginFiresAutomaticallyIfOnlineAndShouldTryAgainIsTrue(): Unit = runBlocking {
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

        verify(handleRemoteLogin).login(any(), any())
    }

    @Test
    fun loginFiresAutomaticallyButOffline() = runBlocking {
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
        val event = SignedOutInfoAnalyticsViewModel.makeSignedOutInfoViewEvent(context)
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
        val event = SignedOutInfoAnalyticsViewModel.makeReAuthEvent(context)
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

        wheneverBlocking { handleRemoteLogin.login(any(), any()) }.thenAnswer(
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
