package uk.gov.onelogin.network

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.authentication.login.refresh.DemonstratingProofOfPossessionManager
import uk.gov.android.network.attestation.ClientAttestationProvider
import uk.gov.android.network.dpop.DPoPProvider
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import uk.gov.onelogin.features.network.provider.ClientAttestationProviderImpl
import uk.gov.onelogin.features.network.provider.DPoPProviderImpl

@Module
@InstallIn(ViewModelComponent::class)
object AppIntegrityProviderModule {
    @Provides
    fun provideDPoPProvider(
        @ApplicationContext context: Context,
        dPoPManager: DemonstratingProofOfPossessionManager,
    ): DPoPProvider = DPoPProviderImpl(context, dPoPManager)

    @Provides
    fun provideClientAttestationProvider(appIntegrity: AppIntegrity): ClientAttestationProvider =
        ClientAttestationProviderImpl(appIntegrity)
}
