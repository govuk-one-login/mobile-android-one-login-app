package uk.gov.onelogin.tokens

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.runBlocking
import uk.gov.android.securestore.AccessControlLevel
import uk.gov.android.securestore.SecureStorageConfigurationAsync
import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.SharedPrefsStoreAsyncV2
import uk.gov.onelogin.core.tokens.domain.idtoken.walletId.WALLET_ID_KEY
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import javax.inject.Named
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SecureStoreSingletonModule::class],
)
object TestSecureStoreSingletonModule {
    @Provides
    @Singleton
    @Named("Token")
    fun providesTokenSecureStore(): SecureStoreAsyncV2 = SharedPrefsStoreAsyncV2()

    @Provides
    @Singleton
    @Named("Open")
    fun providesOpenSecureStore(
        @ApplicationContext
        context: Context,
    ): SecureStoreAsyncV2 =
        SharedPrefsStoreAsyncV2().also {
            val configuration =
                SecureStorageConfigurationAsync(
                    AuthTokenStoreKeys.OPEN_SECURE_STORE_ID,
                    AccessControlLevel.OPEN,
                )
            it.init(context, configuration)
            runBlocking {
                // Pre-populate wallet store ID so WalletScreenViewModel can initialise
                // without a real user session
                it.upsert(WALLET_ID_KEY, "wallet_store_id")
            }
        }
}
