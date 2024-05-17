package uk.gov.onelogin.tokens

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.SharedPrefsStore
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStore
import uk.gov.onelogin.tokens.usecases.AutoInitialiseSecureStoreImpl

@Module
@InstallIn(SingletonComponent::class)
object SecureStoreModule {

    @Provides
    @Singleton
    fun providesSecureStore(): SecureStore = SharedPrefsStore()

    @Provides
    @Singleton
    fun providesAutoInitialiseSecureStore(
        autoInitialiseSecureStoreImpl: AutoInitialiseSecureStoreImpl
    ): AutoInitialiseSecureStore = autoInitialiseSecureStoreImpl
}
