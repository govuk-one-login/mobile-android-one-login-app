package uk.gov.onelogin.libraries.mockcriorchestratorsdk

import uk.gov.onelogin.criorchestrator.features.idcheckwrapper.publicapi.idchecksdkactivestate.IsIdCheckSdkActive
import uk.gov.onelogin.criorchestrator.features.idcheckwrapper.publicapi.idchecksdkactivestate.IsIdCheckSdkActiveProviders
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorAppGraph
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk

/**
 * Mock implementation of [CriOrchestratorSdk] (ID Check SDK) for testing.
 *
 * This is scaffolding so that a future test build flavour can inject it in place of the
 * real [CriOrchestratorSdk] for use with Maestro UI tests, without instantiating the real
 * SDK.
 *
 * The only behaviour it provides is a static answer for whether an ID Check journey is
 * active. This is reached via the `CriOrchestratorSdk.isIdCheckSdkActive` extension, which
 * casts [appGraph] to [IsIdCheckSdkActiveProviders] - so the mock's [appGraph] must
 * implement that provider for the mock to be usable (e.g. by `CheckIdCheckSessionState`).
 *
 * @param isIdCheckSdkActive the static value returned by the mocked [IsIdCheckSdkActive]
 * use case. Defaults to `false` (no ID Check journey in progress).
 */
class MockCriOrchestratorSdk(private val isIdCheckSdkActive: Boolean = false) :
    CriOrchestratorSdk {

    override val appGraph: CriOrchestratorAppGraph =
        object : CriOrchestratorAppGraph, IsIdCheckSdkActiveProviders {
            override fun isIdCheckSdkActive(): IsIdCheckSdkActive =
                IsIdCheckSdkActive { this@MockCriOrchestratorSdk.isIdCheckSdkActive }
        }
}
