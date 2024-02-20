package uk.gov.onelogin.credentialchecker

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface CredentialCheckerModule {
    @Binds
    @ViewModelScoped
    fun bindCredentialChecker(checker: DeviceCredentialChecker): CredentialChecker

    @Binds
    @ViewModelScoped
    fun bindBiometricManager(manager: BiometricManagerImpl): BiometricManager
}
