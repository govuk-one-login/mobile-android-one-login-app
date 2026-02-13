package uk.gov.onelogin.wallet

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCase
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCaseImpl

@InstallIn(SingletonComponent::class)
@Module
object DeleteWalletModule {
    @Provides
    fun provideDeleteWalletData(walletSdk: WalletSdk): DeleteWalletDataUseCase = DeleteWalletDataUseCaseImpl(walletSdk)
}
