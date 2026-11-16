package uk.gov.onelogin.libraries.mocknetwork

import uk.gov.android.network.client.KtorHttpClient
import uk.gov.android.network.log.KtorLogger
import uk.gov.android.network.useragent.UserAgentGenerator

/**
 * Builds a [KtorHttpClient] backed by a [MockEngine][io.ktor.client.engine.mock.MockEngine]
 * instead of the real Android transport.
 *
 * The real [KtorHttpClient] is used deliberately: all of its request building, content
 * negotiation, response validation and provider wiring (authentication / attestation / DPoP)
 * behave exactly as in production, so only the transport is mocked. This keeps the mocked build
 * exercising as much of the real network code as possible.
 *
 * Downstream, every network type (`DefaultNetworkService`, the v1 service and the generic
 * clients) is constructed from this [KtorHttpClient], so replacing it is sufficient to mock the
 * whole network layer.
 */
object MockKtorHttpClientFactory {
    fun create(userAgentGenerator: UserAgentGenerator, config: MockNetworkConfig): KtorHttpClient =
        KtorHttpClient(
            userAgentGenerator = userAgentGenerator,
            logger = KtorLogger.noOp,
            ktorClientEngine = MockNetworkEngineFactory.create(config),
        )
}
