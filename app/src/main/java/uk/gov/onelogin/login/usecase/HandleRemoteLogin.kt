package uk.gov.onelogin.login.usecase

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.android.authentication.login.LoginSession
import uk.gov.android.authentication.login.LoginSessionConfiguration
import uk.gov.android.features.FeatureFlags
import uk.gov.android.onelogin.R
import uk.gov.onelogin.appcheck.AppIntegrity
import uk.gov.onelogin.appcheck.AttestationResult
import uk.gov.onelogin.core.utils.UriParser
import uk.gov.onelogin.features.StsFeatureFlag
import uk.gov.onelogin.tokens.usecases.GetPersistentId
import uk.gov.onelogin.ui.LocaleUtils

interface HandleRemoteLogin {
    suspend fun login(
        launcher: ActivityResultLauncher<Intent>,
        onFailure: () -> Unit
    )
}

@Suppress("LongParameterList")
class HandleRemoteLoginImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val featureFlags: FeatureFlags,
    private val localeUtils: LocaleUtils,
    private val loginSession: LoginSession,
    private val getPersistentId: GetPersistentId,
    private val appIntegrity: AppIntegrity,
    private val uriParser: UriParser
) : HandleRemoteLogin {
    override suspend fun login(
        launcher: ActivityResultLauncher<Intent>,
        onFailure: () -> Unit
    ) {
        val persistentId = getPersistentId()?.takeIf { it.isNotEmpty() }
        handleGetClientAttestation(
            {
                loginSession.present(
                    launcher,
                    configuration = createLoginConfiguration(persistentId)
                )
            },
            onFailure
        )
    }

    private suspend fun handleGetClientAttestation(
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        when (appIntegrity.getClientAttestation()) {
            is AttestationResult.Failure -> onFailure()

            else -> onSuccess()
        }
    }

    private fun createLoginConfiguration(persistentId: String?): LoginSessionConfiguration {
        val locale = localeUtils.getLocaleAsSessionConfig()
        val authorizeEndpoint = uriParser.parse(
            context.getString(
                if (featureFlags[StsFeatureFlag.STS_ENDPOINT]) {
                    R.string.stsUrl
                } else {
                    R.string.openIdConnectBaseUrl
                },
                context.getString(R.string.openIdConnectAuthorizeEndpoint)
            )
        )
        val tokenEndpoint = uriParser.parse(
            context.getString(
                if (featureFlags[StsFeatureFlag.STS_ENDPOINT]) {
                    R.string.stsUrl
                } else {
                    R.string.apiBaseUrl
                },
                context.getString(R.string.tokenExchangeEndpoint)
            )
        )
        val redirectUri = uriParser.parse(
            context.getString(
                R.string.webBaseUrl,
                context.getString(R.string.webRedirectEndpoint)
            )
        )
        val clientId = if (featureFlags[StsFeatureFlag.STS_ENDPOINT]) {
            context.getString(R.string.stsClientId)
        } else {
            context.getString(R.string.openIdConnectClientId)
        }
        val scopes = listOf(LoginSessionConfiguration.Scope.OPENID)

        return LoginSessionConfiguration(
            authorizeEndpoint = authorizeEndpoint,
            clientId = clientId,
            locale = locale,
            redirectUri = redirectUri,
            scopes = scopes,
            tokenEndpoint = tokenEndpoint,
            persistentSessionId = persistentId
        )
    }
}
