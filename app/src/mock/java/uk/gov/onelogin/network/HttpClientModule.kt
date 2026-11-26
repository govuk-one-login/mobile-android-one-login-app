package uk.gov.onelogin.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import uk.gov.android.network.client.KtorHttpClient
import uk.gov.android.network.useragent.UserAgentGenerator
import uk.gov.onelogin.libraries.mocknetwork.MockKtorHttpClientFactory
import uk.gov.onelogin.libraries.mocknetwork.MockNetworkConfig

/**
 * Provides a [KtorHttpClient] backed by a mocked transport, so no request leaves the device.
 *
 * The real client is used with only its engine replaced, so request building, content
 * negotiation, response validation and the authentication/attestation/DPoP providers all behave
 * as they do in a live build.
 */
@Module
@InstallIn(SingletonComponent::class)
object HttpClientModule {
    @Provides
    @Singleton
    fun provideMockNetworkConfig(): MockNetworkConfig = MockNetworkConfig(MockNetworkScenarios.all)

    @Provides
    @Singleton
    fun provideHttpClient(
        userAgentGenerator: UserAgentGenerator,
        config: MockNetworkConfig,
    ): KtorHttpClient = MockKtorHttpClientFactory.create(userAgentGenerator, config)
}
