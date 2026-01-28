package uk.gov.onelogin.utils

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.onelogin.core.utils.ActivityProvider
import uk.gov.onelogin.core.utils.ActivityProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ActivityProviderModule {
    @Binds
    @Singleton
    fun provideActivityProvider(activityProviderImpl: ActivityProviderImpl): ActivityProvider
}
