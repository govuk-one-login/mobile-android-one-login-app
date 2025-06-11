package uk.gov.onelogin.wallet

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.onelogin.core.R
import uk.gov.android.wallet.core.deletedata.DeleteAllDataUseCase
import uk.gov.android.wallet.core.navigation.Navigator
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.android.wallet.sdk.WalletSdkImpl
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCase
import uk.gov.onelogin.features.wallet.domain.DeleteWalletDataUseCaseImpl

@InstallIn(SingletonComponent::class)
@Module
object WalletModule {
    @Suppress("LongParameterList")
    @Provides
    fun provideWalletSdk(
        @ApplicationContext
        context: Context,
        navigator: Navigator,
        genericHttpClient: GenericHttpClient,
        analyticsLogger: AnalyticsLogger,
        deleteAllDataUseCase: DeleteAllDataUseCase,
        localAuthManager: LocalAuthManager
    ): WalletSdk {
        val config = WalletSdk.Configuration(
            clientId = context.resources.getString(R.string.stsClientId),
            authNetworkClient = genericHttpClient,
            analyticsLogger = analyticsLogger,
            localAuthManger = localAuthManager,
            deleteAllDataUseCase = deleteAllDataUseCase
        )
        return WalletSdkImpl(navigator, config)
    }

    @Provides
    fun provideDeleteWalletData(walletSdk: WalletSdk): DeleteWalletDataUseCase {
        return DeleteWalletDataUseCaseImpl(walletSdk)
    }
}
