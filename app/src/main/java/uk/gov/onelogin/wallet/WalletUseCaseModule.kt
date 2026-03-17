package uk.gov.onelogin.wallet

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.wallet.domain.WalletIsEmptyUseCase
import uk.gov.onelogin.features.wallet.domain.WalletIsEmptyUseCaseImpl

@InstallIn(SingletonComponent::class)
@Module
object WalletUseCaseModule {
    @Provides
    fun provideWalletIsEmptyUseCase(walletSdk: WalletSdk): WalletIsEmptyUseCase = WalletIsEmptyUseCaseImpl(walletSdk)
}
