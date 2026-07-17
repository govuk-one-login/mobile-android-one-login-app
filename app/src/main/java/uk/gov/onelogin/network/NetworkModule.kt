package uk.gov.onelogin.network

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.network.client.GenericHttpClient as GenericHttpClientV1
import uk.gov.android.network.client.v2.GenericHttpClient as GenericHttpClientV2
import uk.gov.android.network.client.KtorHttpClient
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.network.online.OnlineCheckerImpl
import uk.gov.android.network.service.DefaultNetworkService
import uk.gov.android.network.service.NetworkService
import uk.gov.android.network.useragent.UserAgent
import uk.gov.android.network.useragent.UserAgentGenerator
import uk.gov.android.network.useragent.UserAgentGeneratorImpl
import uk.gov.android.onelogin.BuildConfig
import uk.gov.android.onelogin.core.R
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NetworkModule {
    @Binds
    fun networkService(
        defaultNetworkService: DefaultNetworkService
    ): NetworkService

    @Binds
    fun genericHttpClientV2(
        ktorHttpClient: KtorHttpClient
    ): GenericHttpClientV2

    @Binds
    fun genericHttpClientV1(
        ktorHttpClient: KtorHttpClient
    ): GenericHttpClientV1

    companion object {
        /**
         * Provides a single uninitialised instance of [DefaultNetworkService].
         *
         * [DefaultNetworkService] is further initialised in [uk.gov.onelogin.MainActivityViewModel]
         */
        @Provides
        @Singleton
        fun provideNetworkService(
            httpClient: GenericHttpClientV2
        ): DefaultNetworkService =
            DefaultNetworkService(httpClient)

        @Provides
        fun provideOnlineChecker(
            @ApplicationContext
            context: Context,
        ): OnlineChecker {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return OnlineCheckerImpl(connectivityManager)
        }

        @Provides
        fun providesUserAgentGenerator(
            @ApplicationContext context: Context,
        ): UserAgentGenerator {
            val userAgentGenerator = UserAgentGeneratorImpl()
            val appName = context.resources.getString(R.string.one_login_app_name)
            userAgentGenerator.setUserAgent(
                UserAgent(
                    appName = appName,
                    versionName = BuildConfig.VERSION_NAME,
                    clientName = BuildConfig.APPLICATION_ID,
                    manufacturer = Build.MANUFACTURER,
                    model = Build.MODEL,
                    sdkVersion = Build.VERSION.SDK_INT,
                    clientVersion = BuildConfig.VERSION_NAME,
                ),
            )
            return userAgentGenerator
        }

        @Suppress("LongParameterList")
        @Provides
        @Singleton
        fun provideHttpClient(userAgentGenerator: UserAgentGenerator): KtorHttpClient {
            val client = KtorHttpClient(userAgentGenerator)
            return client
        }
    }
}
