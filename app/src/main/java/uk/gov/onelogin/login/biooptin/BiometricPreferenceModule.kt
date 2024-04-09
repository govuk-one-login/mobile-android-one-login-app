package uk.gov.onelogin.login.biooptin

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
fun interface BiometricPreferenceModule {
    @Binds
    @Singleton
    fun bindBiometricPreferenceHandler(
        handler: BiometricPreferenceHandlerImpl
    ): BiometricPreferenceHandler
}
