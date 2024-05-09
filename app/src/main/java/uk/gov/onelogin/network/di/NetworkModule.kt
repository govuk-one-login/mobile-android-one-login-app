package uk.gov.onelogin.network.di

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.client.KtorHttpClient
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.network.online.OnlineCheckerImpl
import uk.gov.android.network.useragent.UserAgentGenerator
import uk.gov.android.network.useragent.UserAgentGeneratorImpl
import uk.gov.android.onelogin.R
import uk.gov.onelogin.network.StsAuthenticationProvider
import uk.gov.onelogin.repositiories.TokenRepository

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideOnlineChecker(
        @ApplicationContext
        context: Context
    ): OnlineChecker {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return OnlineCheckerImpl(connectivityManager)
    }

    @Provides
    fun provideHttpClient(
        @ApplicationContext
        context: Context,
        tokenRepository: TokenRepository
    ): GenericHttpClient {
        val userAgentGenerator: UserAgentGenerator = UserAgentGeneratorImpl()
        val client = KtorHttpClient(userAgentGenerator)
        val url = context.getString(R.string.stsUrl)
        val authProvider = StsAuthenticationProvider(
            url,
            tokenRepository,
            client
        )
        client.setAuthenticationProvider(authProvider)
        return client
    }
}
