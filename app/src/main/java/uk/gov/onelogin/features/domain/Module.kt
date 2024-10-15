package uk.gov.onelogin.features.domain

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
fun interface Module {
    @Binds
    fun bindSetFeatureFlags(setFeatureFlags: SetFeatureFlagsImpl): SetFeatureFlags
}
