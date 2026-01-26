package uk.gov.onelogin.login

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.android.authentication.integrity.keymanager.ECKeyManager
import uk.gov.android.authentication.integrity.keymanager.KeyStoreManager
import kotlin.io.encoding.ExperimentalEncodingApi

@Module
@InstallIn(SingletonComponent::class)
object KeyManagerModule {
    @OptIn(ExperimentalEncodingApi::class)
    @Provides
    fun provideECKeyManager(): KeyStoreManager = ECKeyManager()
}
