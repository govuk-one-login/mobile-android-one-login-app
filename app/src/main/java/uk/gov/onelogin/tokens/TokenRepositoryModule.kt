package uk.gov.onelogin.tokens

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.data.TokenRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
fun interface TokenRepositoryModule {
    @Binds
    @Singleton
    fun bindTokenRepository(repo: TokenRepositoryImpl): TokenRepository
}
