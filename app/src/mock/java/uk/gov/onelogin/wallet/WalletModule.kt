package uk.gov.onelogin.wallet

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.network.service.v2.NetworkService
import uk.gov.android.onelogin.core.R
import uk.gov.android.wallet.core.deletedata.DeleteAllDataUseCase
import uk.gov.android.wallet.sdk.WalletSdk
import uk.gov.logging.api.analytics.logging.v3.AnalyticsLogger
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.libraries.mockwalletsdk.MockWalletSdk

/**
 * Provides a mocked [WalletSdk] in place of the real SDK, so no wallet SDK is instantiated.
 */
@InstallIn(SingletonComponent::class)
@Module
object WalletModule {
    @Suppress("LongParameterList")
    @Provides
    fun provideWalletSdk(
        @ApplicationContext
        context: Context,
        networkService: NetworkService,
        analyticsLogger: AnalyticsLogger,
        deleteAllDataUseCase: DeleteAllDataUseCase,
        localAuthManager: LocalAuthManager,
        logger: Logger,
    ): WalletSdk = MockWalletSdk(
        configValue = WalletSdk.ConfigurationV2(
            clientId = context.resources.getString(R.string.stsClientId),
            analyticsLogger = analyticsLogger,
            localAuthManger = localAuthManager,
            deleteAllDataUseCase = deleteAllDataUseCase,
            logger = logger,
            networkService = networkService,
            trustedReaderCertificates = emptyList(),
        ),
        displayAsFullScreenFlow = MutableStateFlow(false),
    )
}
