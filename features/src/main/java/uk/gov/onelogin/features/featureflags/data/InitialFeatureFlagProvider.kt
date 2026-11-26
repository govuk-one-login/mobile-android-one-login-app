package uk.gov.onelogin.features.featureflags.data

import uk.gov.android.featureflags.FeatureFlag
import uk.gov.android.featureflags.FeatureFlags

/**
 * Provides a static set of flags that the application is initialised with.
 *
 * This is intended for use during app intialisation and not for use within business logic.
 *
 * For general access feature flags, use [FeatureFlags].
 */
fun interface InitialFeatureFlagProvider {
    fun provideFeatureFlags(): Set<FeatureFlag>
}
