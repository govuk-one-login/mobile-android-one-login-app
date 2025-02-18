package uk.gov.onelogin.features

import uk.gov.android.featureflags.FeatureFlag

enum class CriOrchestratorFeatureFlag(
    override val id: String = "Cri Orchestrator Card Enabled"
) : FeatureFlag {
    ENABLED
}
