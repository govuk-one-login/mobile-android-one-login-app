package uk.gov.onelogin.features.featureflags.data

import uk.gov.android.featureflags.FeatureFlag

enum class WalletSharingFeatureFlag(override val id: String = "Wallet Sharing Enabled") :
    FeatureFlag {
    ENABLED,
}
