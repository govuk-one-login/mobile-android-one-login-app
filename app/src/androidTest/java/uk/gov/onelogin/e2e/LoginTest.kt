package uk.gov.onelogin.e2e

import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.filters.FlakyTest
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.AppAuthSession
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.LoginSessionConfiguration
import uk.gov.android.authentication.TokenResponse
import uk.gov.android.onelogin.R
import uk.gov.onelogin.FlakyTestRule
import uk.gov.onelogin.MainActivity
import uk.gov.onelogin.TestActivityForResult
import uk.gov.onelogin.credentialchecker.BiometricManager
import uk.gov.onelogin.credentialchecker.BiometricStatus
import uk.gov.onelogin.credentialchecker.CredentialChecker
import uk.gov.onelogin.credentialchecker.CredentialCheckerModule
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

    @get:Rule(order = 3)
    val flakyTestRule = FlakyTestRule()

    override val phoneController = PhoneController(
        phoneActionTimeout = 10000L,
        testNameRule = testNameRule
    )

    @Before
    fun setup() {
        initializeIntents()
    }

    @After
    fun tearDown() {
        releaseIntents()
    }

    private fun initializeIntents() {
        Intents.init()
    }

    private fun releaseIntents() {
        Intents.release()
    }

    @Test
    @FlakyTest
    fun selectingLoginButtonFiresAuthRequest() {
        launchActivity<MainActivity>()
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
                tokenEndpoint = tokenEndpoint
            )

            verify(mockLoginSession).present(any(), eq(loginConfig))
        }
    }

    @Test
    @FlakyTest
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
    @FlakyTest
    fun handleActivityResultWithDataButLoginThrows() {
        whenever(mockLoginSession.finalise(any(), any())).thenThrow(Error())
        setupActivityForResult(
            Intent(
                Intent.ACTION_VIEW,
                Uri.EMPTY
            )
        )

        phoneController.assertElementExists(
            WAIT_FOR_OBJECT_TIMEOUT,
            loginErrorTitle(context)
        )
    }

    @Test
    @FlakyTest
    fun handleActivityResultWithDataUnsecured() {
        goodLogin()
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
    @FlakyTest
    fun handleActivityResultWithDataBioOptIn() {
        goodLogin()
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.SUCCESS)
        setupActivityForResult(
            Intent(Intent.ACTION_VIEW, Uri.EMPTY)
        )

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
    @FlakyTest
    fun handleActivityResultWithDataPasscode() {
        goodLogin()
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
        Intents.intending(hasComponent(TestActivityForResult::class.java.name)).respondWith(
            Instrumentation.ActivityResult(AppAuthSession.REQUEST_CODE_AUTH, returnedIntent)
        )

        launchActivity<MainActivity>().onActivity {
            it.startActivityForResult(
                Intent(it, TestActivityForResult::class.java),
                AppAuthSession.REQUEST_CODE_AUTH
            )
        }
    }

    private fun goodLogin() {
        val tokenResponse = TokenResponse(
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
        whenever(mockLoginSession.finalise(any(), any())).thenAnswer {
            (it.arguments[1] as (TokenResponse) -> Unit).invoke(tokenResponse)
        }
    }

    companion object {
        const val WAIT_FOR_OBJECT_TIMEOUT = 60_000L
    }
}
