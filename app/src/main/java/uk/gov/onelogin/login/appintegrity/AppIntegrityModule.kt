package uk.gov.onelogin.login.appintegrity

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.android.authentication.integrity.AppIntegrityManager
import uk.gov.android.authentication.integrity.FirebaseAppIntegrityManager
import uk.gov.android.authentication.integrity.appcheck.usecase.AppChecker
import uk.gov.android.authentication.integrity.appcheck.usecase.AttestationCaller
import uk.gov.android.authentication.integrity.keymanager.KeyStoreManager
import uk.gov.android.authentication.integrity.model.AppIntegrityConfiguration
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityImpl
import uk.gov.onelogin.features.login.domain.appintegrity.AttestationApiCall

@SuppressWarnings("kotlin:S6517")
@Module
@InstallIn(ViewModelComponent::class)
interface AppIntegrityModule {
    @Binds
    fun attestationCaller(
        attestationApiCall: AttestationApiCall,
    ): AttestationCaller

    @Binds
    fun provideAppIntegrityCheck(
        appIntegrityImpl: AppIntegrityImpl,
    ): AppIntegrity

    companion object {
        @Provides
        fun provideAppIntegrityConfig(
            attestationCaller: AttestationCaller,
            appChecker: AppChecker,
            keyStoreManager: KeyStoreManager,
        ): AppIntegrityConfiguration =
            AppIntegrityConfiguration(
                attestationCaller = attestationCaller,
                appChecker = appChecker,
                keyStoreManager = keyStoreManager,
            )

        @Provides
        fun provideFirebaseTokenManager(
            logger: Logger,
            config: AppIntegrityConfiguration,
        ): AppIntegrityManager = FirebaseAppIntegrityManager(logger, config)
    }
}
