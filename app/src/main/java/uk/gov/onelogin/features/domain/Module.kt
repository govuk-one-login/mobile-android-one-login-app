package uk.gov.onelogin.features.domain

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
fun interface Module {
    @Binds
    fun bindSetFeatureFlags(setFeatureFlags: FeatureFlagSetterImpl): FeatureFlagSetter
}
