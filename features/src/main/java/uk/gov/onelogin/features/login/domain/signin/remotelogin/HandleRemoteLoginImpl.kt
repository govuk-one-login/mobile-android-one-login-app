package uk.gov.onelogin.features.login.domain.signin.remotelogin

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import uk.gov.android.authentication.login.LoginSession
import uk.gov.android.authentication.login.LoginSessionConfiguration
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.data.LoginException
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.utils.LocaleUtils
import uk.gov.onelogin.core.utils.UriParser
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationResult

@Suppress("LongParameterList")
class HandleRemoteLoginImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val localeUtils: LocaleUtils,
    private val loginSession: LoginSession,
    private val getPersistentId: GetPersistentId,
    private val appIntegrity: AppIntegrity,
    private val uriParser: UriParser,
    private val logger: Logger
) : HandleRemoteLogin {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun login(
        launcher: ActivityResultLauncher<Intent>,
        onFailure: () -> Unit
    ) {
        val persistentId = getPersistentId()?.takeIf { it.isNotEmpty() }
        handleGetClientAttestation(
            {
                try {
                    loginSession.present(
                        launcher,
                        configuration = createLoginConfiguration(persistentId)
                    )
                } catch (e: Throwable) {
                    val loginException = LoginException(e)
                    logger.error(
                        loginException.javaClass.simpleName,
                        e.message ?: NO_MESSAGE,
                        loginException
                    )
                    onFailure()
                }
            },
            onFailure
        )
    }

    private suspend fun handleGetClientAttestation(
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        when (appIntegrity.getClientAttestation()) {
            is AttestationResult.Failure -> {
                onFailure()
            }

            else -> onSuccess()
        }
    }

    private fun createLoginConfiguration(persistentId: String?): LoginSessionConfiguration {
        val locale = localeUtils.getLocaleAsSessionConfig()
        val authorizeEndpoint = uriParser.parse(
            "http://webauthn.io/"
        )
        val tokenEndpoint = uriParser.parse(
            context.getString(
                R.string.stsUrl,
                context.getString(R.string.tokenExchangeEndpoint)
            )
        )
        val redirectUri = uriParser.parse(
            context.getString(
                R.string.webBaseUrl,
                context.getString(R.string.webRedirectEndpoint)
            )
        )
        val clientId = context.getString(R.string.stsClientId)
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

    companion object {
        private const val NO_MESSAGE = "No message"
    }
}
