package uk.gov.onelogin.wallet

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.onelogin.R
import uk.gov.android.wallet.sdk.WalletSdk

@InstallIn(SingletonComponent::class)
@Module
object WalletModule {
    @Provides
    fun provideWalletSdk(
        genericHttpClient: GenericHttpClient,
        @ApplicationContext
        context: Context
    ): WalletSdk {
        val config = WalletSdk.Configuration(
            clientId = context.resources.getString(R.string.stsClientId),
            authNetworkClient = genericHttpClient
        )
        val client = WalletSdk.initialise(config)
        return client
    }
}
