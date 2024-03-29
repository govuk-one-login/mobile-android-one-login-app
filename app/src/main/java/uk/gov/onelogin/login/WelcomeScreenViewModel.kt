package uk.gov.onelogin.login

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.LoginSessionConfiguration
import uk.gov.android.features.FeatureFlags
import uk.gov.onelogin.R
import uk.gov.onelogin.features.StsFeatureFlag
import uk.gov.onelogin.network.utils.IOnlineChecker

@HiltViewModel
class WelcomeScreenViewModel @Inject constructor(
    private val loginSession: LoginSession,
    private val featureFlags: FeatureFlags,
    val onlineChecker: IOnlineChecker
) : ViewModel() {
    fun onPrimary(context: Context) {
        val authorizeEndpoint = Uri.parse(
            context.resources.getString(
                if (featureFlags[StsFeatureFlag.STS_ENDPOINT]) {
                    R.string.stsUrl
                } else {
                    R.string.openIdConnectBaseUrl
                },
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
        val clientId = if (featureFlags[StsFeatureFlag.STS_ENDPOINT]) {
            context.resources.getString(R.string.stsClientId)
        } else {
            context.resources.getString(R.string.openIdConnectClientId)
        }

        val scopes = if (featureFlags[StsFeatureFlag.STS_ENDPOINT]) {
            listOf(LoginSessionConfiguration.Scope.STS)
        } else {
            listOf(LoginSessionConfiguration.Scope.OPENID)
        }
        val locale = getLocaleFrom(context)
        loginSession
            .present(
                context as Activity,
                configuration = LoginSessionConfiguration(
                    authorizeEndpoint = authorizeEndpoint,
                    clientId = clientId,
                    locale = locale,
                    redirectUri = redirectUri,
                    scopes = scopes,
                    tokenEndpoint = tokenEndpoint
                )
            )
    }

    private fun getLocaleFrom(context: Context): LoginSessionConfiguration.Locale {
        val currentLocale = ConfigurationCompat.getLocales(context.resources.configuration)[0]
        val locale = when (currentLocale?.language) {
            "cy" -> LoginSessionConfiguration.Locale.CY
            else -> LoginSessionConfiguration.Locale.EN
        }
        return locale
    }
}
