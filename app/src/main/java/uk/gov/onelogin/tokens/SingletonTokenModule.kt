package uk.gov.onelogin.tokens

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsAccessTokenExpiredImpl
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsRefreshTokenExpiredImpl
import uk.gov.onelogin.core.tokens.domain.expirychecks.IsTokenExpired
import uk.gov.onelogin.core.tokens.domain.retrieve.GetAccessTokenExpiryImpl
import uk.gov.onelogin.core.tokens.domain.retrieve.GetRefreshTokenExpiryImpl
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry
import uk.gov.onelogin.core.utils.AccessToken
import uk.gov.onelogin.core.utils.RefreshToken

@Module
@InstallIn(SingletonComponent::class)
interface SingletonTokenModule {
    @AccessToken
    @Binds
    fun bindGetAccessTokenExpiry(
        getTokenExpiry: GetAccessTokenExpiryImpl
    ): GetTokenExpiry

    @RefreshToken
    @Binds
    fun bindRefreshGetTokenExpiry(
        getTokenExpiry: GetRefreshTokenExpiryImpl
    ): GetTokenExpiry

    @AccessToken
    @Binds
    fun bindIsAccessTokenExpired(isTokenExpired: IsAccessTokenExpiredImpl): IsTokenExpired

    @RefreshToken
    @Binds
    fun bindIsRefreshTokenExpired(isTokenExpired: IsRefreshTokenExpiredImpl): IsTokenExpired
}
