package uk.gov.onelogin.core

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.logging.api.CrashLogger
import uk.gov.logging.api.Logger
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.impl.AndroidLogger
import uk.gov.logging.impl.CrashlyticsLogger
import uk.gov.logging.impl.analytics.FirebaseAnalyticsLogger
import uk.gov.onelogin.core.utils.OneLoginInjectionAnnotation

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    fun provideFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics

    @Provides
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics = Firebase.crashlytics

    @OneLoginInjectionAnnotation
    @Provides
    fun provideCrashlyticsLogger(
        crashlytics: FirebaseCrashlytics
    ): CrashLogger = CrashlyticsLogger(crashlytics)

    @OneLoginInjectionAnnotation
    @Provides
    fun provideLogger(
        @OneLoginInjectionAnnotation
        crashLogger: CrashLogger
    ): Logger = AndroidLogger(crashLogger)

    @Provides
    fun providesAnalyticsLoggerAdapter(
        analytics: FirebaseAnalytics,
        @OneLoginInjectionAnnotation
        logger: Logger
    ): AnalyticsLogger = FirebaseAnalyticsLogger(analytics, logger)
}
