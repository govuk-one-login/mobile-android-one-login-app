package uk.gov.onelogin.features.featureflags.data

import javax.inject.Inject
import uk.gov.android.featureflags.FeatureFlag

class InitialFeatureFlagProviderImpl
@Inject
constructor() : InitialFeatureFlagProvider {
    override fun provideFeatureFlags(): Set<FeatureFlag> =
        setOf(CriOrchestratorFeatureFlag.ENABLED, WalletSharingFeatureFlag.ENABLED)
}
