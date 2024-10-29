package uk.gov.onelogin.appcheck.usecase

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.features.FeatureFlags
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.android.onelogin.R
import uk.gov.onelogin.appcheck.AppIntegrity
import uk.gov.onelogin.appcheck.AppIntegrityImpl
import uk.gov.onelogin.appcheck.FirebaseAppCheck
import uk.gov.onelogin.integrity.ClientAttestationManager
import uk.gov.onelogin.integrity.FirebaseClientAttestationManager
import uk.gov.onelogin.integrity.appcheck.AppChecker
import uk.gov.onelogin.integrity.model.AppIntegrityConfiguration

@SuppressWarnings("kotlin:S6517")
@Module
@InstallIn(ViewModelComponent::class)
object AppCheckUseCaseModule {
    @Provides
    fun provideAppIntegrityConfig(
        @ApplicationContext
        context: Context,
        httpClient: GenericHttpClient,
        appChecker: AppChecker
    ): AppIntegrityConfiguration {
        val endpoint = context.getString(R.string.assertionEndpoint)
        return AppIntegrityConfiguration(
            httpClient = httpClient,
            attestationUrl = context.getString(R.string.assertionUrl, endpoint) + "?device=android",
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

    // To be removed once the call to mobile backend is implemented
    @Provides
    fun provideAssertionApiCall(
        @ApplicationContext
        context: Context,
        appIntegrityConfiguration: AppIntegrityConfiguration
    ): AssertionApiCall = AssertionApiCallImpl(context, appIntegrityConfiguration.httpClient)
}
