package uk.gov.onelogin.optin

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import uk.gov.onelogin.optin.data.AnalyticsOptInLocalSource
import uk.gov.onelogin.optin.data.FirebaseAnalyticsOptInSource
import uk.gov.onelogin.optin.domain.repository.AnalyticsOptInRepository
import uk.gov.onelogin.optin.domain.repository.OptInRepository
import uk.gov.onelogin.optin.domain.source.OptInLocalSource
import uk.gov.onelogin.optin.domain.source.OptInRemoteSource

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class Module {

    @Binds
    abstract fun bindOptInLocalSource(
        source: AnalyticsOptInLocalSource
    ): OptInLocalSource

    @Binds
    abstract fun bindOptInRemoteSource(
        source: FirebaseAnalyticsOptInSource
    ): OptInRemoteSource

    @Binds
    abstract fun bindOptInRepository(
        repository: AnalyticsOptInRepository
    ): OptInRepository

    companion object {
        private const val SHARED_PREFS_KEY = "SharedPrefs.key"

        @Provides
        fun provideSharedPreferences(
            @ApplicationContext context: Context
        ): SharedPreferences = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)

        @Provides
        fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @Provides
        fun provideFirebaseAnalytics(
            @ApplicationContext context: Context
        ): FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }
}
