package uk.gov.onelogin.appcheck

import android.content.Context
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.authentication.integrity.appcheck.AppChecker

@Module
@InstallIn(SingletonComponent::class)
object AppCheckModule {
    @Provides
    fun provideAppCheck(
        @ApplicationContext
        context: Context
    ): AppChecker {
        val factory = PlayIntegrityAppCheckProviderFactory.getInstance()
        return FirebaseAppCheck(factory, context)
    }
}
