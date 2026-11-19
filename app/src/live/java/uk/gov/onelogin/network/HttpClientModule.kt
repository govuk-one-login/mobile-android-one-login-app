package uk.gov.onelogin.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import uk.gov.android.network.client.KtorHttpClient
import uk.gov.android.network.useragent.UserAgentGenerator

/**
 * Provides the real [KtorHttpClient], which talks to the network over the Android transport.
 *
 * Every type in [NetworkModule] is built from this client, so the `mock` flavour mocks the whole
 * network layer by providing its own version of this module.
 */
@Module
@InstallIn(SingletonComponent::class)
object HttpClientModule {
    @Provides
    @Singleton
    fun provideHttpClient(userAgentGenerator: UserAgentGenerator): KtorHttpClient =
        KtorHttpClient(userAgentGenerator)
}
