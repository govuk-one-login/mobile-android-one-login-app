package uk.gov.onelogin.libraries.mockcriorchestratorsdk

import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorAppGraph
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk

/**
 * Mock implementation of [CriOrchestratorSdk] (ID Check SDK) for testing.
 *
 * This is currently an empty mock that provides no behaviour. It exists as scaffolding
 * so that a future test build flavour can inject it in place of the real
 * [CriOrchestratorSdk] for use with Maestro UI tests, removing the need to instantiate
 * the real SDK.
 */
class MockCriOrchestratorSdk : CriOrchestratorSdk {

    override val appGraph: CriOrchestratorAppGraph = object : CriOrchestratorAppGraph {}
}
