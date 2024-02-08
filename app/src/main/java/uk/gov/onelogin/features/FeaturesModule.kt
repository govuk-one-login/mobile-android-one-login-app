package uk.gov.onelogin.features

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.android.features.FeatureFlags
import uk.gov.android.features.InMemoryFeatureFlags
import javax.inject.Singleton

@Module
@InstallIn(
    SingletonComponent::class
)
object FeaturesModule {
    @Provides
    @Singleton
    fun providesFeatureFlags(): FeatureFlags = InMemoryFeatureFlags()
}
