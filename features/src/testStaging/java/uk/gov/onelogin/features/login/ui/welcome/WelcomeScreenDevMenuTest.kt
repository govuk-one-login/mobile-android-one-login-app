package uk.gov.onelogin.features.login.ui.welcome

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.LocalAuthManagerImpl
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsManager
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.counter.Counter
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepo
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken
import uk.gov.onelogin.core.tokens.domain.save.SavePersistentId
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.SaveTokenExpiry
import uk.gov.onelogin.core.ui.pages.loading.LoadingScreenAnalyticsViewModel
import uk.gov.onelogin.features.FragmentActivityTestCase
import uk.gov.onelogin.features.login.domain.signin.loginredirect.HandleLoginRedirect
import uk.gov.onelogin.features.login.domain.signin.remotelogin.HandleRemoteLogin
import uk.gov.onelogin.features.login.ui.signin.welcome.SignInAnalyticsViewModel
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreen
import uk.gov.onelogin.features.login.ui.signin.welcome.WelcomeScreenViewModel
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@RunWith(AndroidJUnit4::class)
class WelcomeScreenDevMenuTest : FragmentActivityTestCase() {
    private lateinit var localAuthPreferenceRepo: LocalAuthPreferenceRepo
    private lateinit var deviceBiometricsManager: DeviceBiometricsManager
    private lateinit var localAuthManager: LocalAuthManager
    private lateinit var tokenRepository: TokenRepository
    private lateinit var autoInitialiseSecureStore: AutoInitialiseSecureStore
    private lateinit var verifyIdToken: VerifyIdToken
    private lateinit var navigator: Navigator
    private lateinit var savePersistentId: SavePersistentId
    private lateinit var saveTokenExpiry: SaveTokenExpiry
    private lateinit var handleRemoteLogin: HandleRemoteLogin
    private lateinit var handleLoginRedirect: HandleLoginRedirect
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var onlineChecker: OnlineChecker
    private lateinit var viewModel: WelcomeScreenViewModel
    private lateinit var analytics: AnalyticsLogger
    private lateinit var analyticsViewModel: SignInAnalyticsViewModel
    private lateinit var loadingAnalyticsViewModel: LoadingScreenAnalyticsViewModel
    private lateinit var counter: Counter
    private val logger = SystemLogger()

    private val devButton = hasText(resources.getString(R.string.app_developer_button))

    @Before
    fun setup() {
        localAuthPreferenceRepo = mock()
        deviceBiometricsManager = mock()
        tokenRepository = mock()
        autoInitialiseSecureStore = mock()
        verifyIdToken = mock()
        navigator = mock()
        savePersistentId = mock()
        saveTokenExpiry = mock()
        handleRemoteLogin = mock()
        handleLoginRedirect = mock()
        signOutUseCase = mock()
        counter = mock()
        onlineChecker = mock()
        analytics = mock()
        localAuthManager =
            LocalAuthManagerImpl(
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
                savePersistentId,
                saveTokenExpiry,
                handleRemoteLogin,
                handleLoginRedirect,
                signOutUseCase,
                logger,
                onlineChecker,
                counter
            )
        analyticsViewModel = SignInAnalyticsViewModel(context, analytics)
        loadingAnalyticsViewModel = LoadingScreenAnalyticsViewModel(context, analytics)
        composeTestRule.setContent {
            WelcomeScreen(viewModel, analyticsViewModel, loadingAnalyticsViewModel)
        }
    }

    @Test
    fun verifyDevMenuClick() {
        composeTestRule.onAllNodes(devButton)[0].performClick()

        verify(navigator).openDeveloperPanel()
    }
}
