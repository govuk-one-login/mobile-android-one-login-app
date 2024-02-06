package uk.gov.onelogin.features

import uk.gov.android.features.FeatureFlag

enum class StsFeatureFlag(override val id: String = this.toString()) : FeatureFlag {
    STS_ENDPOINT
}
