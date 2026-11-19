package uk.gov.onelogin.features.wallet.data

import javax.inject.Inject
import uk.gov.android.wallet.sdk.WalletFeatureFlag

class WalletFeatureFlagProviderImpl
@Inject
constructor() : WalletFeatureFlagProvider {
    override fun provideFeatureFlags(): Set<WalletFeatureFlag> =
        setOf(WalletFeatureFlag.sharing(false))
}
