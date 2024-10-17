package uk.gov.onelogin.features

import uk.gov.android.features.FeatureFlag

enum class AppCheckFeatureFlag(override val id: String = "App Check Enabled") : FeatureFlag {
    ENABLED
}
