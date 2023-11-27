package uk.gov.onelogin

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppRoutesModule {

    @Provides
    @Singleton
    fun providesAppRoutes(appRoutes: AppRoutes): IAppRoutes = appRoutes
}
