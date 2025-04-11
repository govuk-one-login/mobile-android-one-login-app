package uk.gov.onelogin.login.localauth

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.LocalAuthManagerImpl
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsManager
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import javax.inject.Singleton
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepo

@Module
@InstallIn(SingletonComponent::class)
object LocalAuthModule {
    @Provides
    @Singleton
    fun bindLocalAuthManager(
        localAuthPreferenceRepo: LocalAuthPreferenceRepo,
        deviceBiometricsManager: DeviceBiometricsManager,
        analyticsLogger: AnalyticsLogger
    ): LocalAuthManager = LocalAuthManagerImpl(
        localAuthPreferenceRepo,
        deviceBiometricsManager,
        analyticsLogger
    )
}
