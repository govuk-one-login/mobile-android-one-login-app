package uk.gov.onelogin.optin

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.optin.data.AnalyticsOptInLocalSource
import uk.gov.onelogin.optin.data.FirebaseAnalyticsOptInSource
import uk.gov.onelogin.optin.domain.repository.AnalyticsOptInRepository
import uk.gov.onelogin.optin.domain.repository.OptInRepository
import uk.gov.onelogin.optin.domain.source.OptInLocalSource
import uk.gov.onelogin.optin.domain.source.OptInRemoteSource

@Module
@InstallIn(ViewModelComponent::class)
internal interface BinderModule {

    @Binds
    fun bindOptInLocalSource(source: AnalyticsOptInLocalSource): OptInLocalSource

    @Binds
    fun bindOptInRemoteSource(source: FirebaseAnalyticsOptInSource): OptInRemoteSource

    @Binds
    fun bindOptInRepository(repository: AnalyticsOptInRepository): OptInRepository
}
