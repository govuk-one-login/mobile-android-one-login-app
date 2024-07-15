package uk.gov.onelogin.appcheck

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppCheckModule {
    @Provides
    fun provideAppCheck(
        @ApplicationContext
        context: Context
    ): AppCheck = FirebaseAppCheck().also {
        it.init(context)
    }
}
