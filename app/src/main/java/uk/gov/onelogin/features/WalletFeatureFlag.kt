package uk.gov.onelogin.features

import uk.gov.android.featureflags.FeatureFlag

enum class WalletFeatureFlag(override val id: String = "Wallet Enabled") : FeatureFlag {
    ENABLED
}
