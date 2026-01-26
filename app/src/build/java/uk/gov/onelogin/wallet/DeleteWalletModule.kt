package uk.gov.onelogin.wallet

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.onelogin.features.developer.ui.securestore.SecureStoreDevOptionsRepository
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCase
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCaseDevOption

@InstallIn(SingletonComponent::class)
@Module
object DeleteWalletModule {
    @Provides
    fun provideDeleteWalletData(
        walletSdk: WalletSdk,
        secureStoreDevOptionsRepository: SecureStoreDevOptionsRepository,
    ): DeleteWalletDataUseCase = DeleteWalletDataUseCaseDevOption(walletSdk, secureStoreDevOptionsRepository)
}
