package uk.gov.onelogin.features.featureflags.data

import uk.gov.android.featureflags.FeatureFlag

enum class LocalAuthBiometricsToggleFeatureFlag(
    override val id: String = "Biometrics Toggle Enabled"
) : FeatureFlag {
    ENABLED
}
