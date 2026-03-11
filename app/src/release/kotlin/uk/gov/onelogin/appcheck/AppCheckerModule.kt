package uk.gov.onelogin.appcheck

import android.content.Context
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.authentication.integrity.appcheck.usecase.AppChecker
import uk.gov.logging.api.Logger
import uk.gov.onelogin.login.appintegrity.FirebaseAppCheck
import uk.gov.onelogin.login.appintegrity.FirebaseAppCheckProvider
import uk.gov.onelogin.login.appintegrity.FirebaseAppCheckProviderImpl

@Module
@InstallIn(SingletonComponent::class)
object AppCheckerModule {
    @Provides
    fun provideAppCheckProvider(
        @ApplicationContext
        context: Context,
    ): FirebaseAppCheckProvider {
        val factory = PlayIntegrityAppCheckProviderFactory.getInstance()
        return FirebaseAppCheckProviderImpl(factory, context)
    }

    @Provides
    fun provideAppCheck(
        firebaseAppCheckProvider: FirebaseAppCheckProvider,
        logger: Logger,
    ): AppChecker = FirebaseAppCheck(firebaseAppCheckProvider, logger)
}
