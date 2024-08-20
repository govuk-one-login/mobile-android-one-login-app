package uk.gov.onelogin.login.ui.welcome

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.LoginSessionConfiguration
import uk.gov.android.features.FeatureFlags
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.onelogin.R
import uk.gov.onelogin.features.StsFeatureFlag
import uk.gov.onelogin.tokens.usecases.GetPersistentId
import uk.gov.onelogin.ui.LocaleUtils

@HiltViewModel
class WelcomeScreenViewModel @Inject constructor(
    private val loginSession: LoginSession,
    private val featureFlags: FeatureFlags,
    private val getPersistentId: GetPersistentId,
    val onlineChecker: OnlineChecker
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
                if (featureFlags[StsFeatureFlag.STS_ENDPOINT]) {
                    R.string.stsUrl
                } else {
                    R.string.apiBaseUrl
                },
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

        val scopes = listOf(LoginSessionConfiguration.Scope.OPENID)

        val locale = LocaleUtils.getLocaleAsSessionConfig(context)

        viewModelScope.launch {
            loginSession
                .present(
                    context as Activity,
                    configuration = LoginSessionConfiguration(
                        authorizeEndpoint = authorizeEndpoint,
                        clientId = clientId,
                        locale = locale,
                        redirectUri = redirectUri,
                        scopes = scopes,
                        tokenEndpoint = tokenEndpoint,
                        persistentSessionId = getPersistentId()
                    )
                )
        }
    }
}
