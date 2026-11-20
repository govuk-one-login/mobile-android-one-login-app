package uk.gov.onelogin.wallet

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.features.wallet.data.AndroidWalletCertificateProvider
import uk.gov.onelogin.features.wallet.data.WalletCertificateProvider

/**
 * Provides the trusted ReaderAuth certificates, which are read from resources each environment
 * defines for itself, so this is shared by every flavour.
 */
@InstallIn(SingletonComponent::class)
@Module
object WalletCertificateModule {
    @Provides
    fun provideWalletCertificateProvider(
        @ApplicationContext
        context: Context,
        logger: Logger,
    ): WalletCertificateProvider = AndroidWalletCertificateProvider(
        context = context,
        logger = logger,
    )
}
