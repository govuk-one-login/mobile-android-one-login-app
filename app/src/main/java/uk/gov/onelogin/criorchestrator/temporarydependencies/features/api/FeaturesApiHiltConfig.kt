package uk.gov.idcheck.features.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import uk.gov.idcheck.features.api.permissions.ActivityPermissionConditions
import uk.gov.idcheck.features.api.permissions.PermissionConditions

object FeaturesApiHiltConfig {
    @Module
    @InstallIn(
        SingletonComponent::class
    )
    object FeatureFlagsModule {
        @Provides
        @Singleton
        fun providesFeatureFlags(flags: InMemoryFeatureFlags): FeatureFlags = flags
    }

    @Module
    @InstallIn(ActivityComponent::class)
    object PermissionConditionsModule {
        @Provides
        @ActivityScoped
        fun providesPermissionConditions(
            conditions: ActivityPermissionConditions
        ): PermissionConditions = conditions
    }
}
