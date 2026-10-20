package uk.gov.onelogin.utils

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import uk.gov.onelogin.core.utils.ActivityProvider
import uk.gov.onelogin.core.utils.ActivityProviderImpl

@Module
@InstallIn(SingletonComponent::class)
interface ActivityProviderModule {
    @Binds
    @Singleton
    fun provideActivityProvider(activityProviderImpl: ActivityProviderImpl): ActivityProvider
}
