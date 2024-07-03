package uk.gov.onelogin.e2e

import android.net.Uri
import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.LoginSessionConfiguration
import uk.gov.android.onelogin.R
import uk.gov.onelogin.MainActivity
import uk.gov.onelogin.e2e.controller.PhoneController
import uk.gov.onelogin.e2e.controller.TestCase
import uk.gov.onelogin.e2e.selectors.BySelectors.loginButton
import uk.gov.onelogin.login.authentication.LoginSessionModule
import uk.gov.onelogin.ui.LocaleUtils

@HiltAndroidTest
@UninstallModules(LoginSessionModule::class)
class SuccessfulLoginTest : TestCase() {
    @BindValue
    val mockLoginSession: LoginSession = mock()

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

    companion object {
        const val WAIT_FOR_OBJECT_TIMEOUT = 5000L
    }
}
