package uk.gov.onelogin.login.ui.welcome

import android.content.Context
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.LoginSessionConfiguration
import uk.gov.android.authentication.LoginSessionConfiguration.Locale
import uk.gov.android.features.FeatureFlags
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.network.useragent.UserAgentGenerator
import uk.gov.android.onelogin.R
import uk.gov.android.securestore.SecureStore
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.v3dot1.logger.logEventV3Dot1
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.core.analytics.AnalyticsModule
import uk.gov.onelogin.features.FeaturesModule
import uk.gov.onelogin.features.StsFeatureFlag
import uk.gov.onelogin.login.authentication.LoginSessionModule
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.navigation.NavigatorModule
import uk.gov.onelogin.network.di.NetworkModule
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.ui.error.ErrorRoutes

@HiltAndroidTest
@UninstallModules(
    LoginSessionModule::class,
    FeaturesModule::class,
    NetworkModule::class,
    NavigatorModule::class,
    AnalyticsModule::class
)
class WelcomeScreenTest : TestCase() {

    @BindValue
    val loginSession: LoginSession = mock()

    @BindValue
    val featureFlags: FeatureFlags = mock()

    @BindValue
    val onlineChecker: OnlineChecker = mock()

    @BindValue
    val userAgentGenerator: UserAgentGenerator = mock()

    @BindValue
    val httpClient: GenericHttpClient = mock()

    @BindValue
    val mockNavigator: Navigator = mock()

    @BindValue
    val analytics: AnalyticsLogger = mock()

    @Inject
    @Named("Open")
    lateinit var secureStore: SecureStore

    private var shouldTryAgainCalled = false
    private val persistentId = "id"

    @Before
    fun setup() {
        hiltRule.inject()
        shouldTryAgainCalled = false
    }

    private val signInTitle = hasText(resources.getString(R.string.app_signInTitle))
    private val signInSubTitle = hasText(resources.getString(R.string.app_signInBody))
    private val signInButton = hasText(resources.getString(R.string.app_signInButton))
    private val signInIcon =
        hasContentDescription(resources.getString(R.string.app_signInIconDescription))

    @Test
    fun verifyComponents() {
        composeTestRule.setContent {
            WelcomeScreen()
        }

        composeTestRule.onNode(signInTitle).assertIsDisplayed()
        composeTestRule.onNode(signInSubTitle).assertIsDisplayed()
        composeTestRule.onNode(signInButton).assertIsDisplayed()
        composeTestRule.onNode(signInIcon).assertIsDisplayed()
    }

    @Test
    fun opensWebLoginViaCustomTab() = runBlocking {
        whenever(onlineChecker.isOnline()).thenReturn(true)
        whenever(featureFlags[StsFeatureFlag.STS_ENDPOINT]).thenReturn(false)

        composeTestRule.setContent {
            WelcomeScreen()
        }

        whenWeClickSignIn()
        val authorizeEndpoint = Uri.parse(
            context.resources.getString(
                R.string.openIdConnectBaseUrl,
                context.resources.getString(R.string.openIdConnectAuthorizeEndpoint)
            )
        )
        val tokenEndpoint = Uri.parse(
            context.resources.getString(
                R.string.apiBaseUrl,
                context.resources.getString(R.string.tokenExchangeEndpoint)
            )
        )
        val redirectUri = Uri.parse(
            context.resources.getString(
                R.string.webBaseUrl,
                context.resources.getString(R.string.webRedirectEndpoint)
            )
        )
        val clientId = context.resources.getString(R.string.openIdConnectClientId)
        val loginSessionConfig = LoginSessionConfiguration(
            authorizeEndpoint = authorizeEndpoint,
            clientId = clientId,
            locale = Locale.EN,
            redirectUri = redirectUri,
            scopes = listOf(LoginSessionConfiguration.Scope.OPENID),
            tokenEndpoint = tokenEndpoint
        )

        verify(loginSession).present(
            any(),
            eq(loginSessionConfig)
        )
    }

    @Test
    fun opensWebLoginViaCustomTab_StsFlagOn() = runBlocking {
        whenever(onlineChecker.isOnline()).thenReturn(true)
        whenever(featureFlags[StsFeatureFlag.STS_ENDPOINT]).thenReturn(true)
        composeTestRule.setContent {
            WelcomeScreen()
        }

        whenWeClickSignIn()

        val authorizeEndpoint = Uri.parse(
            context.resources.getString(
                R.string.stsUrl,
                context.resources.getString(R.string.openIdConnectAuthorizeEndpoint)
            )
        )
        val tokenEndpoint = Uri.parse(
            context.resources.getString(
                R.string.stsUrl,
                context.resources.getString(R.string.tokenExchangeEndpoint)
            )
        )
        val redirectUri = Uri.parse(
            context.resources.getString(
                R.string.webBaseUrl,
                context.resources.getString(R.string.webRedirectEndpoint)
            )
        )
        val clientId = context.resources.getString(R.string.stsClientId)
        val loginSessionConfig = LoginSessionConfiguration(
            authorizeEndpoint = authorizeEndpoint,
            clientId = clientId,
            locale = Locale.EN,
            redirectUri = redirectUri,
            scopes = listOf(LoginSessionConfiguration.Scope.OPENID),
            tokenEndpoint = tokenEndpoint
        )

        verify(loginSession).present(
            any(),
            eq(loginSessionConfig)
        )
    }

    @Test
    fun opensWebLoginViaCustomTab_StsFlagOn_goodPersistentId() = runBlocking {
        whenever(onlineChecker.isOnline()).thenReturn(true)
        whenever(featureFlags[StsFeatureFlag.STS_ENDPOINT]).thenReturn(true)
        setPersistentId(persistentId)

        composeTestRule.setContent {
            WelcomeScreen()
        }

        whenWeClickSignIn()

        val authorizeEndpoint = Uri.parse(
            context.resources.getString(
                R.string.stsUrl,
                context.resources.getString(R.string.openIdConnectAuthorizeEndpoint)
            )
        )
        val tokenEndpoint = Uri.parse(
            context.resources.getString(
                R.string.stsUrl,
                context.resources.getString(R.string.tokenExchangeEndpoint)
            )
        )
        val redirectUri = Uri.parse(
            context.resources.getString(
                R.string.webBaseUrl,
                context.resources.getString(R.string.webRedirectEndpoint)
            )
        )
        val clientId = context.resources.getString(R.string.stsClientId)
        val loginSessionConfig = LoginSessionConfiguration(
            authorizeEndpoint = authorizeEndpoint,
            clientId = clientId,
            locale = Locale.EN,
            redirectUri = redirectUri,
            scopes = listOf(LoginSessionConfiguration.Scope.OPENID),
            tokenEndpoint = tokenEndpoint,
            persistentSessionId = persistentId
        )

        verify(loginSession).present(
            any(),
            eq(loginSessionConfig)
        )
        deletePersistentId()
    }

    @Test
    fun opensWebLoginViaCustomTab_StsFlagOn_emptyPersistentId() = runBlocking {
        whenever(onlineChecker.isOnline()).thenReturn(true)
        whenever(featureFlags[StsFeatureFlag.STS_ENDPOINT]).thenReturn(true)
        setPersistentId("")

        composeTestRule.setContent {
            WelcomeScreen()
        }

        whenWeClickSignIn()

        val authorizeEndpoint = Uri.parse(
            context.resources.getString(
                R.string.stsUrl,
                context.resources.getString(R.string.openIdConnectAuthorizeEndpoint)
            )
        )
        val tokenEndpoint = Uri.parse(
            context.resources.getString(
                R.string.stsUrl,
                context.resources.getString(R.string.tokenExchangeEndpoint)
            )
        )
        val redirectUri = Uri.parse(
            context.resources.getString(
                R.string.webBaseUrl,
                context.resources.getString(R.string.webRedirectEndpoint)
            )
        )
        val clientId = context.resources.getString(R.string.stsClientId)
        val loginSessionConfig = LoginSessionConfiguration(
            authorizeEndpoint = authorizeEndpoint,
            clientId = clientId,
            locale = Locale.EN,
            redirectUri = redirectUri,
            scopes = listOf(LoginSessionConfiguration.Scope.OPENID),
            tokenEndpoint = tokenEndpoint
        )

        verify(loginSession).present(
            any(),
            eq(loginSessionConfig)
        )
        deletePersistentId()
    }

    @Test
    fun shouldTryAgainCalledOnPageLoad() {
        composeTestRule.setContent {
            WelcomeScreen(
                shouldTryAgain = {
                    shouldTryAgainCalled = true
                    false
                }
            )
        }
        assertTrue(shouldTryAgainCalled)
    }

    @Test
    fun loginFiresAutomaticallyIfOnlineAndShouldTryAgainIsTrue() = runBlocking {
        whenever(onlineChecker.isOnline()).thenReturn(true)
        whenever(featureFlags[StsFeatureFlag.STS_ENDPOINT]).thenReturn(true)
        composeTestRule.setContent {
            WelcomeScreen(
                shouldTryAgain = {
                    true
                }
            )
        }

        val authorizeEndpoint = Uri.parse(
            context.resources.getString(
                R.string.stsUrl,
                context.resources.getString(R.string.openIdConnectAuthorizeEndpoint)
            )
        )
        val tokenEndpoint = Uri.parse(
            context.resources.getString(
                R.string.stsUrl,
                context.resources.getString(R.string.tokenExchangeEndpoint)
            )
        )
        val redirectUri = Uri.parse(
            context.resources.getString(
                R.string.webBaseUrl,
                context.resources.getString(R.string.webRedirectEndpoint)
            )
        )
        val clientId = context.resources.getString(R.string.stsClientId)
        val loginSessionConfig = LoginSessionConfiguration(
            authorizeEndpoint = authorizeEndpoint,
            clientId = clientId,
            locale = Locale.EN,
            redirectUri = redirectUri,
            scopes = listOf(LoginSessionConfiguration.Scope.OPENID),
            tokenEndpoint = tokenEndpoint
        )

        verify(loginSession).present(
            any(),
            eq(loginSessionConfig)
        )
    }

    @Test
    fun navigateToErrorScreenIfNotOnlineAndShouldTryAgainIsTrue() = runBlocking {
        whenever(onlineChecker.isOnline()).thenReturn(false)
        whenever(featureFlags[StsFeatureFlag.STS_ENDPOINT]).thenReturn(true)
        composeTestRule.setContent {
            WelcomeScreen(
                shouldTryAgain = {
                    true
                }
            )
        }
        verify(mockNavigator).navigate(ErrorRoutes.Offline)
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
            WelcomeScreen()
        }

        verify(analytics).logEventV3Dot1(event)
    }

    @Test
    fun signInAnalyticsLogOnSignInButton() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val event = SignInAnalyticsViewModel.makeSignInEvent(context)
        whenever(onlineChecker.isOnline()).thenReturn(true)
        composeTestRule.setContent {
            WelcomeScreen()
        }
        whenWeClickSignIn()
        verify(analytics).logEventV3Dot1(event)
    }

    private fun whenWeClickSignIn() {
        composeTestRule.onNode(signInButton).performClick()
    }

    private fun givenWeAreOffline() {
        whenever(onlineChecker.isOnline()).thenReturn(false)
        composeTestRule.setContent {
            WelcomeScreen()
        }
    }

    private fun itOpensErrorScreen() {
        verify(mockNavigator).navigate(ErrorRoutes.Offline)
    }

    private suspend fun setPersistentId(id: String) {
        secureStore.upsert(
            key = Keys.PERSISTENT_ID_KEY,
            value = id
        )
    }

    private fun deletePersistentId() {
        secureStore.delete(
            key = Keys.PERSISTENT_ID_KEY
        )
    }

    @Test
    fun previewTest() {
        // Absolute cop out
        composeTestRule.setContent {
            WelcomePreview()
        }
    }
}
