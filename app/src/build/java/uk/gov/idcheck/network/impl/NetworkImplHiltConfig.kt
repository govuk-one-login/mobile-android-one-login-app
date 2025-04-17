package uk.gov.idcheck.network.impl

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import uk.gov.idcheck.network.api.backend.BackendApi
import uk.gov.idcheck.network.api.txma.TxmaApi
import uk.gov.idcheck.network.api.usecases.GetBioTokenV2FromApi
import uk.gov.idcheck.network.impl.backend.BackendApiImpl
import uk.gov.idcheck.network.impl.txma.TxmaApiImpl
import uk.gov.idcheck.network.impl.usecases.GetBioTokenV2FromApiImpl
import javax.inject.Singleton
import uk.gov.idcheck.network.api.biotoken.BioTokenApi
import uk.gov.idcheck.network.impl.biotoken.BioTokenV2Api

object NetworkImplHiltConfig {
    @InstallIn(SingletonComponent::class)
    @Module
    object BackendApiModule {
        @Provides
        @Singleton
        fun providesBackendApi(api: BackendApiImpl): BackendApi = api
    }

    /**
     * Hilt dependency injection (DI) module for the [BioTokenApi].
     *
     * Scoped for view models.
     */
    @InstallIn(ViewModelComponent::class)
    @Module
    object BioTokenApiModule {
        @Provides
        @ViewModelScoped
        fun providesBioTokenApi(api: BioTokenV2Api): BioTokenApi = api
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    object BioTokenFetcherModule {
        @Provides
        @ViewModelScoped
        fun providesGetBioTokenV2FromApi(
            getBioTokenV2FromApi: GetBioTokenV2FromApiImpl,
        ): GetBioTokenV2FromApi = getBioTokenV2FromApi
    }

    @InstallIn(ActivityRetainedComponent::class)
    @Module
    object TxmaApiBuildModule {
        @Provides
        @ActivityRetainedScoped
        fun providesTxmaApi(api: TxmaApiImpl): TxmaApi = api
    }
}
