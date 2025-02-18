package uk.gov.onelogin.features

import uk.gov.android.features.FeatureFlag

enum class CriOrchestratorFeatureFlag(
    override val id: String = "Cri Orchestrator Card Enabled"
) : FeatureFlag {
    ENABLED
}
