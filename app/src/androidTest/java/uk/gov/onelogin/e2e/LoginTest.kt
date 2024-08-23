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
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.app.ActivityOptionsCompat
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
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
import uk.gov.onelogin.e2e.controller.BySelectorEntry
import uk.gov.onelogin.e2e.controller.PhoneController
import uk.gov.onelogin.e2e.controller.TestCase
import uk.gov.onelogin.e2e.selectors.BySelectors.bioOptInTitle
import uk.gov.onelogin.e2e.selectors.BySelectors.continueButton
import uk.gov.onelogin.e2e.selectors.BySelectors.enableBiometricsButton
import uk.gov.onelogin.e2e.selectors.BySelectors.homeTitle
import uk.gov.onelogin.e2e.selectors.BySelectors.loginButton
import uk.gov.onelogin.e2e.selectors.BySelectors.loginErrorTitle
import uk.gov.onelogin.e2e.selectors.BySelectors.loginTitle
import uk.gov.onelogin.e2e.selectors.BySelectors.passcodeInfoTitle
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

//    @get:Rule(order = 3)
//    val flakyTestRule = FlakyTestRule()

    @get:Rule(order = 4)
    val composeRule = createAndroidComposeRule<HiltTestActivity>()

    private val persistentId = "cc893ece-b6bd-444d-9bb4-dec6f5778e50"
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

    override val phoneController = PhoneController(
        phoneActionTimeout = 10000L,
        testNameRule = testNameRule
    )

    @Before
    fun setup() {
        hiltRule.inject()
        secureStore.delete(Keys.PERSISTENT_ID_KEY)
    }

    //    @FlakyTest
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
        phoneController.apply {
            click(
                WAIT_FOR_OBJECT_TIMEOUT,
                loginButton(context) to "Press login button"
            )

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
    }

    //    @FlakyTest
    @Test
    fun selectingLoginButtonFiresAuthRequestWithPersistentId() {
        tokenRepository.setTokenResponse(tokenResponse)

        startApp()

        phoneController.apply {
            click(
                WAIT_FOR_OBJECT_TIMEOUT,
                loginButton(context) to "Press login button"
            )

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
    }

    //    @FlakyTest
    @Test
    fun selectingLoginButtonFiresAuthRequestWithPersistentIdFromSecureStore() = runTest {
        secureStore.upsert(Keys.PERSISTENT_ID_KEY, persistentId)

        startApp()

        phoneController.apply {
            click(
                WAIT_FOR_OBJECT_TIMEOUT,
                loginButton(context) to "Press login button"
            )

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
    }

    @Test
//    @FlakyTest
    @Ignore("Currently UI navigation is not working")
    fun handleActivityResultNullData() {
        setupActivityForResult(
            Intent()
        )

        phoneController.assertElementExists(
            WAIT_FOR_OBJECT_TIMEOUT,
            loginTitle(context)
        )
        verify(mockLoginSession, times(0)).finalise(any(), any())
    }

    @Test
//    @FlakyTest
    @Ignore("Currently UI navigation is not working")
    fun handleActivityResultWithDataButLoginThrows() {
        whenever(mockLoginSession.finalise(any(), any())).thenThrow(Error())
        setupActivityForResult(
            Intent(
                Intent.ACTION_VIEW,
                Uri.EMPTY
            )
        )

        phoneController.click(
            WAIT_FOR_OBJECT_TIMEOUT,
            BySelectorEntry(loginButton(context), "login click")
        )

        phoneController.assertElementExists(
            WAIT_FOR_OBJECT_TIMEOUT,
            loginErrorTitle(context)
        )
    }

    @Test
//    @FlakyTest
    @Ignore("Currently UI navigation is not working")
    fun handleActivityResultWithDataUnsecured() {
        mockGoodLogin()
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(false)
        setupActivityForResult(
            Intent(
                Intent.ACTION_VIEW,
                Uri.EMPTY
            )
        )

        phoneController.assertElementExists(
            WAIT_FOR_OBJECT_TIMEOUT,
            passcodeInfoTitle(context)
        )
        phoneController.click(
            WAIT_FOR_OBJECT_TIMEOUT,
            continueButton(context) to "Continue button"
        )
        phoneController.assertElementExists(
            WAIT_FOR_OBJECT_TIMEOUT,
            homeTitle(context)
        )
    }

    @Test
//    @FlakyTest
    @Ignore("Currently UI navigation is not working")
    fun handleActivityResultWithDataBioOptIn() = runTest {
        mockGoodLogin()
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.SUCCESS)
        setupActivityForResult(
            Intent(Intent.ACTION_VIEW, Uri.EMPTY)
        )
        phoneController.click(
            WAIT_FOR_OBJECT_TIMEOUT,
            BySelectorEntry(loginButton(context), "login click")
        )

        phoneController.waitUntilIdle(10000L)
        phoneController.assertElementExists(
            WAIT_FOR_OBJECT_TIMEOUT,
            bioOptInTitle(context)
        )
        phoneController.click(
            WAIT_FOR_OBJECT_TIMEOUT,
            enableBiometricsButton(context) to "Enable biometrics button"
        )
        phoneController.assertElementExists(
            WAIT_FOR_OBJECT_TIMEOUT,
            homeTitle(context)
        )
    }

    @Test
//    @FlakyTest
    @Ignore("Currently UI navigation is not working")
    fun handleActivityResultWithDataPasscode() {
        mockGoodLogin()
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.UNKNOWN)
        setupActivityForResult(
            Intent(Intent.ACTION_VIEW, Uri.EMPTY)
        )

        phoneController.assertElementExists(
            WAIT_FOR_OBJECT_TIMEOUT,
            homeTitle(context)
        )
        phoneController.screenshot("home")
    }

    private fun setupActivityForResult(returnedIntent: Intent) {
        whenever(mockLoginSession.present(any(), any())).thenAnswer {
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

    private fun mockGoodLogin() {
        whenever(mockLoginSession.finalise(any(), any())).thenAnswer {
            (it.arguments[1] as (TokenResponse) -> Unit).invoke(tokenResponse)
        }
    }

    companion object {
        const val WAIT_FOR_OBJECT_TIMEOUT = 20_000L
    }
}
