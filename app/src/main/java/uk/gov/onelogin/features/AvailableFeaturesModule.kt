package uk.gov.onelogin.features

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AvailableFeaturesModule {
    @Provides
    @Singleton
    fun providesAvailableFeatures(): AvailableFeatures = AvailableFeatures()
}
