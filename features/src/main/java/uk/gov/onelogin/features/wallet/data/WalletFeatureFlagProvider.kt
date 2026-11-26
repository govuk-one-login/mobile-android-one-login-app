package uk.gov.onelogin.features.wallet.data

import uk.gov.android.wallet.sdk.WalletFeatureFlag

fun interface WalletFeatureFlagProvider {
    fun provideFeatureFlags(): Set<WalletFeatureFlag>
}
