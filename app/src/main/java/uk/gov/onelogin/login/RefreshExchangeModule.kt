package uk.gov.onelogin.login

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchange
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchangeImpl

@InstallIn(ViewModelComponent::class)
@Module
interface RefreshExchangeModule {
    @Binds
    fun provideRefreshExchangeManager(impl: RefreshExchangeImpl): RefreshExchange
}
