package uk.gov.onelogin

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.utils.AccessToken
import uk.gov.onelogin.core.utils.ActivityProvider
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchange
import uk.gov.onelogin.features.network.provider.StsAuthenticationProvider
import uk.gov.onelogin.features.signout.domain.SignOutUseCase
import javax.inject.Inject

@Suppress("LongParameterList")
@HiltViewModel
class MainActivityViewModel
    @Inject
    constructor(
        private val activityProvider: ActivityProvider,
        @param:ApplicationContext
        private val context: Context,
        private val genericHttpClient: GenericHttpClient,
        private val tokenRepository: TokenRepository,
        @param:AccessToken
        private val isAccessTokenExpired: IsTokenExpired,
        private val navigator: Navigator,
        private val refreshExchange: RefreshExchange,
        private val signOutUseCase: SignOutUseCase,
        private val logger: Logger
    ) : ViewModel() {
        fun setHttpClientAuthProvider() {
            val endpoint = context.getString(R.string.tokenExchangeEndpoint)
            val url = context.getString(R.string.stsUrl, endpoint)
            val provider =
                StsAuthenticationProvider(
                    activityProvider,
                    url,
                    tokenRepository,
                    isAccessTokenExpired,
                    genericHttpClient,
                    navigator,
                    refreshExchange,
                    signOutUseCase,
                    logger
                )
            genericHttpClient.setAuthenticationProvider(provider)
        }
    }
