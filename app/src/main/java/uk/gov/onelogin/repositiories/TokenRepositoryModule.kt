package uk.gov.onelogin.repositiories

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
fun interface TokenRepositoryModule {
    @Binds
    @Singleton
    fun bindTokenRepository(
        repo: TokenRepositoryImpl
    ): TokenRepository
}
