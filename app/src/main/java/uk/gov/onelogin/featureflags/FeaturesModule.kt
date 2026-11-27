package uk.gov.onelogin.featureflags

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.featureflags.InMemoryFeatureFlags
import uk.gov.onelogin.features.featureflags.data.AvailableFeatures
import uk.gov.onelogin.features.featureflags.data.InitialFeatureFlagProvider
import uk.gov.onelogin.features.featureflags.data.InitialFeatureFlagProviderImpl
import uk.gov.onelogin.features.featureflags.domain.FeatureFlagSetter
import uk.gov.onelogin.features.featureflags.domain.FeatureFlagSetterImpl

@Module
@InstallIn(
    SingletonComponent::class,
)
interface FeaturesModule {
    companion object {
        @Provides
        @Singleton
        fun providesFeatureFlags(
            initialFeatureFlagProvider: InitialFeatureFlagProvider,
        ): FeatureFlags = InMemoryFeatureFlags(initialFeatureFlagProvider.provideFeatureFlags())

        @Provides
        @Singleton
        fun providesAvailableFeatures(): AvailableFeatures = AvailableFeatures()
    }

    @Binds
    @Singleton
    fun bindInitialFeatureFlags(impl: InitialFeatureFlagProviderImpl): InitialFeatureFlagProvider

    @Binds
    @Singleton
    fun bindFeatureFlagSetter(impl: FeatureFlagSetterImpl): FeatureFlagSetter
}
