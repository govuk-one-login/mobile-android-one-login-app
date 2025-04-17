package uk.gov.onelogin.criorchestrator.temporarydependencies

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApplicationResourcesModule {
    @Provides
    @Singleton
    fun providesApplicationResources(
        @ApplicationContext
        context: Context,
    ): Resources = context.resources
}
