package uk.gov.onelogin.tokens

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import uk.gov.android.securestore.AccessControlLevel
import uk.gov.android.securestore.SecureStorageConfiguration
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.SharedPrefsStore
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStoreImpl

@Module
@InstallIn(SingletonComponent::class)
object SecureStoreModule {

    @Provides
    @Singleton
    @Named("Token")
    fun providesTokenSecureStore(): SecureStore = SharedPrefsStore()

    @Provides
    @Singleton
    @Named("Open")
    fun providesOpenSecureStore(
        @ApplicationContext
        context: Context
    ): SecureStore = SharedPrefsStore().also {
        val configuration = SecureStorageConfiguration(
            Keys.OPEN_SECURE_STORE_ID,
            AccessControlLevel.OPEN
        )
        it.init(context, configuration)
    }

    @Provides
    @Singleton
    fun providesAutoInitialiseSecureStore(
        autoInitialiseSecureStoreImpl: AutoInitialiseSecureStoreImpl
    ): AutoInitialiseSecureStore = autoInitialiseSecureStoreImpl
}
