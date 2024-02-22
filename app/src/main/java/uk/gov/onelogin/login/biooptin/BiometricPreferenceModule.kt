package uk.gov.onelogin.login.biooptin

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
fun interface BiometricPreferenceModule {
    @Binds
    @ViewModelScoped
    fun bindBiometricPreferenceHandler(
        handler: BiometricPreferenceHandlerImpl
    ): BiometricPreferenceHandler
}
