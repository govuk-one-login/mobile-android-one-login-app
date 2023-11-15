package uk.gov.onelogin.network.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AuthCodeExchangeModule {

    @Binds
    @Singleton
    fun bindsAuthCodeExchange(authCodeExchange: AuthCodeExchange): IAuthCodeExchange
}
