package uk.gov.onelogin.network.di

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.network.client.KtorHttpClient
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.network.online.OnlineCheckerImpl
import uk.gov.android.network.useragent.UserAgent
import uk.gov.android.network.useragent.UserAgentGenerator
import uk.gov.android.network.useragent.UserAgentGeneratorImpl
import uk.gov.android.onelogin.BuildConfig
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
    fun providesUserAgentGenerator(@ApplicationContext context: Context): UserAgentGenerator {
        val userAgentGenerator = UserAgentGeneratorImpl()
        val appName = context.resources.getString(R.string.app_name)
        userAgentGenerator.setUserAgent(
            UserAgent(
                appName = appName,
                versionName = BuildConfig.VERSION_NAME,
                clientName = BuildConfig.APPLICATION_ID,
                manufacturer = Build.MANUFACTURER,
                model = Build.MODEL,
                sdkVersion = Build.VERSION.SDK_INT,
                clientVersion = BuildConfig.VERSION_NAME
            )
        )
        return userAgentGenerator
    }

    @Provides
    fun provideHttpClient(
        @ApplicationContext
        context: Context,
        userAgentGenerator: UserAgentGenerator,
        tokenRepository: TokenRepository
    ): GenericHttpClient {
        val client = KtorHttpClient(userAgentGenerator)
        val endpoint = context.getString(R.string.tokenExchangeEndpoint)
        val url = context.getString(R.string.stsUrl, endpoint)
        val authProvider = StsAuthenticationProvider(
            url,
            tokenRepository,
            client
        )
        client.setAuthenticationProvider(authProvider)
        return client
    }
}
