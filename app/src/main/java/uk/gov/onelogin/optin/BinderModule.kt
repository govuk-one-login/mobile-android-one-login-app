package uk.gov.onelogin.optin

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.onelogin.features.analyticsoptin.data.AnalyticsOptInRepository
import uk.gov.onelogin.features.analyticsoptin.data.OptInRepository
import uk.gov.onelogin.features.analyticsoptin.domain.AnalyticsOptInLocalSource
import uk.gov.onelogin.features.analyticsoptin.domain.FirebaseAnalyticsOptInSource
import uk.gov.onelogin.features.analyticsoptin.domain.source.OptInLocalSource
import uk.gov.onelogin.features.analyticsoptin.domain.source.OptInRemoteSource

@Module
@InstallIn(SingletonComponent::class)
internal interface BinderModule {
    @Binds
    fun bindOptInLocalSource(source: AnalyticsOptInLocalSource): OptInLocalSource

    @Binds
    fun bindOptInRemoteSource(source: FirebaseAnalyticsOptInSource): OptInRemoteSource

    @Binds
    fun bindOptInRepository(repository: AnalyticsOptInRepository): OptInRepository
}
