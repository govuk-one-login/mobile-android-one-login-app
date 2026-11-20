package uk.gov.onelogin.features.wallet.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface WalletFeatureFlagModule {
    @Binds
    fun bindWalletFeatureFlagProvider(
        impl: WalletFeatureFlagProviderImpl,
    ): WalletFeatureFlagProvider
}
