package uk.gov.onelogin.appcheck

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.authentication.integrity.appcheck.AppChecker
import uk.gov.android.onelogin.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object AppCheckModule {
    @Provides
    fun provideAppCheck(
        @ApplicationContext
        context: Context
    ): AppChecker {
        val factory = DebugAppCheckProviderFactory.getInstance()
        return FirebaseAppCheck(factory).also {
            val key = Firebase.app.persistenceKey
            context.getSharedPreferences(
                "com.google.firebase.appcheck.debug.store.$key",
                MODE_PRIVATE
            ).edit().apply {
                putString(
                    "com.google.firebase.appcheck.debug.DEBUG_SECRET",
                    BuildConfig.AppCheckDebugSecret
                )
                commit()
            }

            it.init(context)
        }
    }
}
