package uk.gov.onelogin.libraries.mockcriorchestratorsdk

import uk.gov.onelogin.criorchestrator.features.idcheckwrapper.publicapi.idchecksdkactivestate.IsIdCheckSdkActive
import uk.gov.onelogin.criorchestrator.features.idcheckwrapper.publicapi.idchecksdkactivestate.IsIdCheckSdkActiveProviders
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorAppGraph
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk

/**
 * Mock implementation of [CriOrchestratorSdk] (which wraps the ID Check SDK) that can
 * replace the real implementation in tests.
 *
 * @param isIdCheckSdkActive the static value reported for whether an ID Check journey is
 * active. Defaults to `false`.
 */
class MockCriOrchestratorSdk(private val isIdCheckSdkActive: Boolean = false) :
    CriOrchestratorSdk {

    // The CriOrchestratorSdk.isIdCheckSdkActive extension casts appGraph to
    // IsIdCheckSdkActiveProviders, so appGraph must implement it for the mock to be usable.
    override val appGraph: CriOrchestratorAppGraph =
        object : CriOrchestratorAppGraph, IsIdCheckSdkActiveProviders {
            override fun isIdCheckSdkActive(): IsIdCheckSdkActive =
                IsIdCheckSdkActive { this@MockCriOrchestratorSdk.isIdCheckSdkActive }
        }
}
