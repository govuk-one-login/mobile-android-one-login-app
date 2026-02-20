package uk.gov.onelogin.tokens

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.securestore.AccessControlLevel
import uk.gov.android.securestore.SecureStorageConfigurationAsync
import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.SharedPrefsStoreAsyncV2
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStoreImpl
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.features.developer.ui.securestore.SecureStoreDevOptionsRepository
import uk.gov.onelogin.features.developer.ui.securestore.SecureStoreDevOptionsRepositoryImpl
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecureStoreSingletonModule {
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
        }
}

@Module
@InstallIn(ViewModelComponent::class)
object SecureStoreViewModelModule {
    @Provides
    fun providesAutoInitialiseSecureStore(
        autoInitialiseSecureStoreImpl: AutoInitialiseSecureStoreImpl
    ): AutoInitialiseSecureStore = autoInitialiseSecureStoreImpl
}

@Module
@InstallIn(SingletonComponent::class)
object SecureStoreDevModeModule {
    @Provides
    @Singleton
    fun providesSecureStoreRepository(): SecureStoreDevOptionsRepository = SecureStoreDevOptionsRepositoryImpl()
}
