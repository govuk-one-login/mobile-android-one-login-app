package uk.gov.onelogin.login

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreId
import uk.gov.onelogin.core.tokens.domain.retrieve.GetWalletStoreIdImpl
import uk.gov.onelogin.features.login.domain.validateWalletStoreId.ValidateWalletStoreId

@Module
@InstallIn(SingletonComponent::class)
object ValidateWalletStoreIdModule {
    @Provides
    fun providesValidateWalletStoreId(getWalletStoreId: GetWalletStoreId,): ValidateWalletStoreId =
        ValidateWalletStoreId(getWalletStoreId)
}

@Module
@InstallIn(SingletonComponent::class)
interface GetWalletModule {
    @Binds
    fun bindGetWalletId(getWalletStoreId: GetWalletStoreIdImpl): GetWalletStoreId
}
