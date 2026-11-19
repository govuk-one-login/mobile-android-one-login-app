package uk.gov.onelogin.libraries.mocknetwork

import android.annotation.SuppressLint
import uk.gov.android.network.client.KtorHttpClient
import uk.gov.android.network.log.KtorLogger
import uk.gov.android.network.useragent.UserAgentGenerator

/**
 * Builds a [KtorHttpClient] backed by a [MockEngine][io.ktor.client.engine.mock.MockEngine]
 * instead of the real Android transport.
 */
object MockKtorHttpClientFactory {
    // The engine-accepting KtorHttpClient constructor is annotated @VisibleForTesting, but using
    // it from this mock library is exactly its intended purpose.
    @SuppressLint("VisibleForTests")
    fun create(userAgentGenerator: UserAgentGenerator, config: MockNetworkConfig): KtorHttpClient =
        KtorHttpClient(
            userAgentGenerator = userAgentGenerator,
            logger = KtorLogger.noOp,
            ktorClientEngine = MockNetworkEngineFactory.create(config),
        )
}
