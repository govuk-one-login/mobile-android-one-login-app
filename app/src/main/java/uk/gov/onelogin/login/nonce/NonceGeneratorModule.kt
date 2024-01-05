package uk.gov.onelogin.login.nonce

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NonceGeneratorModule {

    @Provides
    @Singleton
    fun bindsNonceGenerator(nonceGenerator: NonceGenerator): INonceGenerator = nonceGenerator
}
