package uk.gov.onelogin.tokens

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.onelogin.tokens.usecases.GetTokenExpiry
import uk.gov.onelogin.tokens.usecases.GetTokenExpiryImpl
import uk.gov.onelogin.tokens.usecases.IsAccessTokenExpired
import uk.gov.onelogin.tokens.usecases.IsAccessTokenExpiredImpl

@Module
@InstallIn(SingletonComponent::class)
interface SingletonTokenModule {
    @Binds
    fun bindGetTokenExpiry(
        getTokenExpiry: GetTokenExpiryImpl
    ): GetTokenExpiry

    @Binds
    fun bindIsAccessTokenExpired(isTokenExpired: IsAccessTokenExpiredImpl): IsAccessTokenExpired
}
