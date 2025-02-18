package uk.gov.onelogin.features

import uk.gov.android.featureflags.FeatureFlag

enum class AppCheckFeatureFlag(override val id: String = "App Check Enabled") : FeatureFlag {
    ENABLED
}
