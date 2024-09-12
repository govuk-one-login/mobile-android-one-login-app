package uk.gov.onelogin.e2e

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityOptionsCompat
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.LoginSessionConfiguration
import uk.gov.android.authentication.TokenResponse
import uk.gov.android.onelogin.R
import uk.gov.android.securestore.SecureStore
import uk.gov.onelogin.HiltTestActivity
import uk.gov.onelogin.OneLoginApp
import uk.gov.onelogin.credentialchecker.BiometricManager
import uk.gov.onelogin.credentialchecker.BiometricStatus
import uk.gov.onelogin.credentialchecker.CredentialChecker
import uk.gov.onelogin.credentialchecker.CredentialCheckerModule
import uk.gov.onelogin.e2e.controller.TestCase
import uk.gov.onelogin.login.authentication.LoginSessionModule
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.ui.LocaleUtils

@HiltAndroidTest
@UninstallModules(LoginSessionModule::class, CredentialCheckerModule::class)
class LoginTest : TestCase() {
    @BindValue
    val mockLoginSession: LoginSession = mock()

    @BindValue
    val mockCredChecker: CredentialChecker = mock()

    @BindValue
    val mockBiometricManager: BiometricManager = mock()

    @Inject
    lateinit var tokenRepository: TokenRepository

    @Inject
    @Named("Open")
    lateinit var secureStore: SecureStore

    @get:Rule(order = 3)
    val composeRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        secureStore.delete(Keys.PERSISTENT_ID_KEY)
    }

    @Test
    fun selectingLoginButtonFiresAuthRequestNoPersistentId() {
        tokenRepository.setTokenResponse(
            TokenResponse(
                tokenType = "type",
                accessToken = "access",
                accessTokenExpirationTime = 1L,
                idToken = ""
            )
        )
        startApp()
        clickOptOut()
        clickLogin()

        val authorizeUrl = Uri.parse(
            resources.getString(
                R.string.stsUrl,
                resources.getString(R.string.openIdConnectAuthorizeEndpoint)
            )
        )
        val redirectUrl = Uri.parse(
            resources.getString(
                R.string.webBaseUrl,
                resources.getString(R.string.webRedirectEndpoint)
            )
        )
        val tokenEndpoint = Uri.parse(
            context.resources.getString(
                R.string.stsUrl,
                context.resources.getString(
                    R.string.tokenExchangeEndpoint
                )
            )
        )
        val loginConfig = LoginSessionConfiguration(
            authorizeEndpoint = authorizeUrl,
            clientId = resources.getString(R.string.stsClientId),
            locale = LocaleUtils.getLocaleAsSessionConfig(context),
            redirectUri = redirectUrl,
            scopes = listOf(LoginSessionConfiguration.Scope.OPENID),
            tokenEndpoint = tokenEndpoint,
            persistentSessionId = null
        )

        verify(mockLoginSession).present(any(), eq(loginConfig))
    }

    @Test
    fun selectingLoginButtonFiresAuthRequestWithPersistentId() {
        tokenRepository.setTokenResponse(tokenResponse)

        startApp()
        clickOptOut()
        clickLogin()

        val authorizeUrl = Uri.parse(
            resources.getString(
                R.string.stsUrl,
                resources.getString(R.string.openIdConnectAuthorizeEndpoint)
            )
        )
        val redirectUrl = Uri.parse(
            resources.getString(
                R.string.webBaseUrl,
                resources.getString(R.string.webRedirectEndpoint)
            )
        )
        val tokenEndpoint = Uri.parse(
            context.resources.getString(
                R.string.stsUrl,
                context.resources.getString(
                    R.string.tokenExchangeEndpoint
                )
            )
        )
        val loginConfig = LoginSessionConfiguration(
            authorizeEndpoint = authorizeUrl,
            clientId = resources.getString(R.string.stsClientId),
            locale = LocaleUtils.getLocaleAsSessionConfig(context),
            redirectUri = redirectUrl,
            scopes = listOf(LoginSessionConfiguration.Scope.OPENID),
            tokenEndpoint = tokenEndpoint,
            persistentSessionId = persistentId
        )

        verify(mockLoginSession).present(any(), eq(loginConfig))
    }

    @Test
    fun selectingLoginButtonFiresAuthRequestWithPersistentIdFromSecureStore() {
        runBlocking {
            secureStore.upsert(Keys.PERSISTENT_ID_KEY, persistentId)
        }
        startApp()
        clickOptOut()
        clickLogin()

        val authorizeUrl = Uri.parse(
            resources.getString(
                R.string.stsUrl,
                resources.getString(R.string.openIdConnectAuthorizeEndpoint)
            )
        )
        val redirectUrl = Uri.parse(
            resources.getString(
                R.string.webBaseUrl,
                resources.getString(R.string.webRedirectEndpoint)
            )
        )
        val tokenEndpoint = Uri.parse(
            context.resources.getString(
                R.string.stsUrl,
                context.resources.getString(
                    R.string.tokenExchangeEndpoint
                )
            )
        )
        val loginConfig = LoginSessionConfiguration(
            authorizeEndpoint = authorizeUrl,
            clientId = resources.getString(R.string.stsClientId),
            locale = LocaleUtils.getLocaleAsSessionConfig(context),
            redirectUri = redirectUrl,
            scopes = listOf(LoginSessionConfiguration.Scope.OPENID),
            tokenEndpoint = tokenEndpoint,
            persistentSessionId = persistentId
        )

        verify(mockLoginSession).present(any(), eq(loginConfig))
    }

    // App remains on sign in page when not data is returned in intent from login
    @Test
    fun handleActivityResultNullData() {
        setupActivityForResult(
            Intent()
        )

        clickLogin()

        nodeWithTextExists(resources.getString(R.string.app_signInTitle))
        verify(mockLoginSession, times(0)).finalise(any(), any())
    }

    @Test
    fun handleActivityResultWithDataButLoginThrows() {
        whenever(mockLoginSession.finalise(any(), any())).thenThrow(Error())
        setupActivityForResult(
            Intent(
                Intent.ACTION_VIEW,
                Uri.EMPTY
            )
        )
        clickOptOut()
        composeRule.onNodeWithText("Sign in").performClick()
        nodeWithTextExists("There was a problem signing you in")
    }

    @Test
    fun handleActivityResultWithDataUnsecured() {
        mockGoodLogin()
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(false)
        setupActivityForResult(
            Intent(
                Intent.ACTION_VIEW,
                Uri.EMPTY
            )
        )

        clickOptOut()
        clickLogin()
        nodeWithTextExists(resources.getString(R.string.app_noPasscodePatternSetupTitle))
        composeRule.onNodeWithText(resources.getString(R.string.app_continue))
        nodeWithTextExists(resources.getString(R.string.app_homeTitle))
    }

    @Test
    fun handleActivityResultWithDataBioOptIn() {
        mockGoodLogin()
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.SUCCESS)
        setupActivityForResult(
            Intent(Intent.ACTION_VIEW, Uri.EMPTY)
        )
        clickOptOut()
        clickLogin()

        nodeWithTextExists(resources.getString(R.string.app_enableBiometricsTitle))
        composeRule.onNodeWithText(resources.getString(R.string.app_enableBiometricsButton))
        nodeWithTextExists(resources.getString(R.string.app_homeTitle))
    }

    @Test
    fun handleActivityResultWithDataPasscode() {
        mockGoodLogin()
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.UNKNOWN)
        setupActivityForResult(
            Intent(Intent.ACTION_VIEW, Uri.EMPTY)
        )

        clickOptOut()
        clickLogin()

        nodeWithTextExists(resources.getString(R.string.app_homeTitle))
    }

    private fun setupActivityForResult(returnedIntent: Intent) {
        whenever(mockLoginSession.present(any(), any())).thenAnswer {
            @Suppress("unchecked_cast")
            (it.arguments[0] as ActivityResultLauncher<Intent>).launch(Intent())
        }
        composeRule.setContent {
            val registryOwner = object : ActivityResultRegistryOwner {
                override val activityResultRegistry: ActivityResultRegistry
                    get() = object : ActivityResultRegistry() {
                        override fun <I : Any?, O : Any?> onLaunch(
                            requestCode: Int,
                            contract: ActivityResultContract<I, O>,
                            input: I,
                            options: ActivityOptionsCompat?
                        ) {
                            this.dispatchResult(requestCode, Activity.RESULT_OK, returnedIntent)
                        }
                    }
            }
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
                OneLoginApp()
            }
        }
    }

    private fun startApp() {
        composeRule.setContent {
            OneLoginApp()
        }
    }

    private fun clickOptOut() {
        composeRule.waitForIdle()
        val doNotShare = composeRule.onNodeWithText(
            resources.getString(R.string.app_doNotShareAnalytics)
        )
        val isOnOptInScreen = doNotShare.isDisplayed()
        if (isOnOptInScreen) {
            doNotShare.performClick()
        }
    }

    private fun clickLogin() {
        composeRule.waitForIdle()
        composeRule.onNodeWithText(resources.getString(R.string.app_signInButton)).performClick()
    }

    private fun nodeWithTextExists(text: String) {
        composeRule.waitForIdle()
        composeRule.onNodeWithText(text).isDisplayed()
    }

    private fun mockGoodLogin() {
        whenever(mockLoginSession.finalise(any(), any())).thenAnswer {
            @Suppress("unchecked_cast")
            (it.arguments[1] as (TokenResponse) -> Unit).invoke(tokenResponse)
        }
    }

    companion object {
        private const val persistentId = "cc893ece-b6bd-444d-9bb4-dec6f5778e50"
        private val tokenResponse = TokenResponse(
            tokenType = "test",
            accessToken = "test",
            accessTokenExpirationTime = 1L,
            idToken = "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjE2ZGI2NTg3LTU0NDUtNDVkNi1hN" +
                "2Q5LTk4NzgxZWJkZjkzZCJ9.eyJhdWQiOiJHRVV6a0V6SVFVOXJmYmdBWmJzal9fMUVOUU0iLCJ" +
                "pc3MiOiJodHRwczovL3Rva2VuLmJ1aWxkLmFjY291bnQuZ292LnVrIiwic3ViIjoiOWQwZjIxZG" +
                "UtMmZkNy00MjdiLWE2ZGYtMDdjZDBkOTVlM2I2IiwicGVyc2lzdGVudF9pZCI6ImNjODkzZWNlL" +
                "WI2YmQtNDQ0ZC05YmI0LWRlYzZmNTc3OGU1MCIsImlhdCI6MTcyMTk5ODE3OCwiZXhwIjoxNzIx" +
                "OTk4MzU4LCJub25jZSI6InRlc3Rfbm9uY2UiLCJlbWFpbCI6Im1vY2tAZW1haWwuY29tIiwiZW1" +
                "haWxfdmVyaWZpZWQiOnRydWV9.G1uQ9z2i-214kEmmtK7hEHRsgqJdk7AXjz_CaJDiuuqSyHZ4W" +
                "48oE1karDBA-pKWpADdBpHeUC-eCjjfBObjOg"
        )
    }
}
