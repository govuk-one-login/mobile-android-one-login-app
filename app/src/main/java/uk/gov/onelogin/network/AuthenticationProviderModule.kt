package uk.gov.onelogin.network

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.network.auth.AuthenticationProvider
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.network.di.StsUrl
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.utils.AccessToken
import uk.gov.onelogin.core.utils.ActivityProvider
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchange
import uk.gov.onelogin.features.network.provider.StsAuthenticationProvider
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@Module
@InstallIn(ViewModelComponent::class)
interface AuthenticationProviderModule {
    @Binds
    fun authenticationProvider(
        stsAuthenticationProvider: StsAuthenticationProvider,
    ): AuthenticationProvider

    companion object {
        @Provides
        @StsUrl
        fun provideStsUrl(
            @ApplicationContext context: Context,
        ): String =
            context.getString(
                R.string.stsUrl,
                context.getString(R.string.tokenExchangeEndpoint),
            )
    }
}
