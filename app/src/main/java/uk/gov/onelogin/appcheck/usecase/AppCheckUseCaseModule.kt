package uk.gov.onelogin.appcheck.usecase

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.authentication.integrity.ClientAttestationManager
import uk.gov.android.authentication.integrity.FirebaseClientAttestationManager
import uk.gov.android.authentication.integrity.appcheck.AppChecker
import uk.gov.android.authentication.integrity.model.AppIntegrityConfiguration
import uk.gov.android.authentication.integrity.usecase.AttestationCaller
import uk.gov.android.features.FeatureFlags
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.onelogin.appcheck.AppIntegrity
import uk.gov.onelogin.appcheck.AppIntegrityImpl

@SuppressWarnings("kotlin:S6517")
@Module
@InstallIn(ViewModelComponent::class)
object AppCheckUseCaseModule {
    @Provides
    fun provideAppIntegrityConfig(
        attestationCaller: AttestationCaller,
        appChecker: AppChecker
    ): AppIntegrityConfiguration {
        return AppIntegrityConfiguration(
            attestationCaller = attestationCaller,
            appChecker = appChecker
        )
    }

    @Provides
    fun provideFirebaseTokenManager(
        config: AppIntegrityConfiguration
    ): ClientAttestationManager {
        return FirebaseClientAttestationManager(config)
    }

    @Provides
    fun provideAppIntegrityCheck(
        featureFlags: FeatureFlags,
        appCheck: ClientAttestationManager
    ): AppIntegrity {
        return AppIntegrityImpl(
            featureFlags,
            appCheck
        )
    }

    @Provides
    fun provideAssertionApiCall(
        @ApplicationContext
        context: Context,
        genericHttpClient: GenericHttpClient
    ): AttestationCaller = AttestationApiCall(context, genericHttpClient)
}
