package uk.gov.onelogin.wallet

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.onelogin.core.R
import uk.gov.android.wallet.core.deletedata.DeleteAllDataUseCase
import uk.gov.android.wallet.core.navigation.Navigator
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.android.wallet.sdk.WalletSdkImpl
import uk.gov.logging.api.Logger
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.core.ui.wallet.WalletAppDisplayer
import uk.gov.onelogin.core.ui.wallet.WalletAppDisplayerImpl
import uk.gov.android.network.service.v2.NetworkService

@InstallIn(SingletonComponent::class)
@Module
object WalletModule {
    @Suppress("LongParameterList")
    @Provides
    fun provideWalletSdk(
        @ApplicationContext
        context: Context,
        navigator: Navigator,
        networkService: NetworkService,
        analyticsLogger: AnalyticsLogger,
        deleteAllDataUseCase: DeleteAllDataUseCase,
        localAuthManager: LocalAuthManager,
        logger: Logger,
    ): WalletSdk {
        val config =
            WalletSdk.Configuration(
                clientId = context.resources.getString(R.string.stsClientId),
                analyticsLogger = analyticsLogger,
                localAuthManger = localAuthManager,
                deleteAllDataUseCase = deleteAllDataUseCase,
                logger = logger,
                networkService = networkService
            )
        return WalletSdkImpl(navigator, config, context)
    }
}

@InstallIn(ViewModelComponent::class)
@Module
interface WalletUiModule {

    @Binds
    fun bindWalletAppDisplayer(
        walletAppDisplayerImpl: WalletAppDisplayerImpl,
    ): WalletAppDisplayer

}


