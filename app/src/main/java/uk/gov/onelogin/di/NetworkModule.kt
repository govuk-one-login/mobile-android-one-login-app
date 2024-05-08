package uk.gov.onelogin.di

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.network.online.OnlineChecker
import uk.gov.android.network.online.OnlineCheckerImpl

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideOnlineChecker(
        @ApplicationContext
        context: Context
    ): OnlineChecker {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return OnlineCheckerImpl(connectivityManager)
    }
}
