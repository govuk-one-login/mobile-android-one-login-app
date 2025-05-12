package uk.gov.onelogin.wallet

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.onelogin.features.wallet.data.WalletRepository
import uk.gov.onelogin.features.wallet.data.WalletRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object WalletRepositoryModule {
    @Provides
    @Singleton
    fun provideWalletRepository(): WalletRepository = WalletRepositoryImpl()
}
