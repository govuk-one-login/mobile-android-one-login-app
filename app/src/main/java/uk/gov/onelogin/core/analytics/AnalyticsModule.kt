package uk.gov.onelogin.core.analytics

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.logging.api.Logger
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.impl.analytics.FirebaseAnalyticsLogger

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    fun provideFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics

    @Provides
    fun providesAnalyticsLoggerAdapter(
        analytics: FirebaseAnalytics,
        logger: Logger
    ): AnalyticsLogger = FirebaseAnalyticsLogger(analytics, logger)
}
