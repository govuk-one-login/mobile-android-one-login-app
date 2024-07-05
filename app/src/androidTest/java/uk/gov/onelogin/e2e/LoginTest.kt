package uk.gov.onelogin.e2e

import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Before
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
            20000L,
            homeTitle(context)
        )
    }

    @Test
    fun handleActivityResultWithDataPasscode() {
        goodLogin()
        whenever(mockCredChecker.isDeviceSecure()).thenReturn(true)
        whenever(mockCredChecker.biometricStatus()).thenReturn(BiometricStatus.UNKNOWN)
        setupActivityForResult(
            Intent(Intent.ACTION_VIEW, Uri.EMPTY)
        )

        phoneController.assertElementExists(
            20000L,
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
            idToken = "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjE2ZGI2NTg3L" +
                "TU0NDUtNDVkNi1hN2Q5LTk4NzgxZWJkZjkzZCJ9.eyJhdWQiOiJHRVV6a0V6" +
                "SVFVOXJmYmdBWmJzal9fMUVOUU0iLCJpc3MiOiJodHRwczovL3Rva2VuLmJ" +
                "1aWxkLmFjY291bnQuZ292LnVrIiwic3ViIjoiMmU5YzdlMTYtZmQ4NS00Yz" +
                "A5LThkM2EtZDA2MzljMTUzMzc4IiwiaWF0IjoxNzE3NTc4NzY4LCJleHAiOj" +
                "E3MTc1Nzg5NDgsIm5vbmNlIjoidGVzdF9ub25jZSIsImVtYWlsIjoibW9ja0" +
                "BlbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZX0.j1xQsDeX37Z8Bn" +
                "B-Aq4ovVfGq1ADa9cycYJHtlcSfZwSh_0c_FowQPN7MJjRHBdAE1pnjqtnbi" +
                "c14VFnJuMCoA"
        )
        whenever(mockLoginSession.finalise(any(), any())).thenAnswer {
            (it.arguments[1] as (TokenResponse) -> Unit).invoke(tokenResponse)
        }
    }

    companion object {
        const val WAIT_FOR_OBJECT_TIMEOUT = 5000L
    }
}
