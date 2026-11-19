package uk.gov.onelogin.wallet

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.core.ui.wallet.WalletDisplayer
import uk.gov.onelogin.core.ui.wallet.WalletDisplayerImpl

@InstallIn(ViewModelComponent::class)
@Module
interface WalletUiModule {

    @Binds
    fun bindWalletAppDisplayer(walletAppDisplayerImpl: WalletDisplayerImpl): WalletDisplayer
}
